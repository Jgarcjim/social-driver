
package com.liferay.social.driver.web.content;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletPreferences;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

import com.liferay.blogs.recent.bloggers.web.constants.RecentBloggersPortletKeys;
import com.liferay.dynamic.data.mapping.io.DDMFormXSDDeserializerUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.dynamic.data.mapping.util.DDMXMLUtil;
import com.liferay.journal.configuration.JournalServiceConfigurationKeys;
import com.liferay.journal.content.web.constants.JournalContentPortletKeys;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.UserEmailAddressException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.Country;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.model.Region;
import com.liferay.portal.model.User;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.CountryServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.RegionServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.social.model.SocialRequest;
import com.liferay.portlet.social.model.SocialRequestConstants;
import com.liferay.portlet.social.service.SocialRequestLocalServiceUtil;
import com.liferay.social.activities.web.constants.SocialActivitiesPortletKeys;
import com.liferay.social.driver.web.constants.SocialDriverConstants;

public class UserManager {

	public static long getUserId(long companyId, String themeId) throws Exception {

		User user;
		String first = getRndStr(SocialDriverConstants.FIRST_NAMES);
		try {
			user = UserLocalServiceUtil.getUserByScreenName(companyId, first.toLowerCase());
		} catch (NoSuchUserException ex) {
			user = addUser(first, companyId, themeId);
		}
		return user.getUserId();

	}

	public static void addSocialRequest(User user, User receiverUser, boolean confirm) throws Exception {

		SocialRequest socialRequest = SocialRequestLocalServiceUtil.addRequest(user.getUserId(), 0,
				User.class.getName(), user.getUserId(), 1, StringPool.BLANK, receiverUser.getUserId());

		if (confirm) {
			SocialRequestLocalServiceUtil.updateRequest(socialRequest.getRequestId(),
					SocialRequestConstants.STATUS_CONFIRM, new ThemeDisplay());
		}
	}

	public static String addPortletId(Layout layout, String portletId, String columnId) throws Exception {

		LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();

		portletId = layoutTypePortlet.addPortletId(0, portletId, columnId, -1, false);

		// addResources(layout, portletId);
		updateLayout(layout);

		return portletId;
	}

	public static void updateLayout(Layout layout) throws Exception {

		LayoutLocalServiceUtil.updateLayout(layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
				layout.getTypeSettings());
	}

	public static void configureJournalContent(Layout layout, Group group, String portletId, String articleId)
			throws Exception {

		PortletPreferences portletSetup = PortletPreferencesFactoryUtil.getLayoutPortletSetup(layout, portletId);

		if (group == null) {
			portletSetup.setValue("groupId", String.valueOf(layout.getGroupId()));
		} else {
			portletSetup.setValue("groupId", String.valueOf(group.getGroupId()));
		}
		portletSetup.setValue("articleId", articleId);

		portletSetup.store();
	}

	public static void configurePortletTitle(Layout layout, String portletId, String title) throws Exception {

		PortletPreferences portletSetup = PortletPreferencesFactoryUtil.getLayoutPortletSetup(layout, portletId);

		portletSetup.setValue("portletSetupUseCustomTitle", String.valueOf(Boolean.TRUE));
		portletSetup.setValue("portletSetupTitle_en_US", title);

		portletSetup.store();
	}

	public static String getRndStr(String[] ar) {

		return ar[(int) (Math.random() * (double) ar.length)];
	}

	public synchronized static User addUser(String firstName, long companyId, String themeId) throws Exception {

		String lastName = getRndStr(SocialDriverConstants.LAST_NAMES);
		String job = getRndStr(SocialDriverConstants.JOB_TITLES);

		Group guestGroup = GroupLocalServiceUtil.getGroup(companyId, GroupConstants.GUEST);

		User user;

		ServiceContext serviceContext = new ServiceContext();

		long[] groupIds = new long[] { guestGroup.getGroupId() };

		long[] roleIds = new long[] {};

		try {
			user = UserLocalServiceUtil.addUser(UserLocalServiceUtil.getDefaultUserId(companyId), companyId, false,
					firstName, firstName, false, firstName.toLowerCase(), firstName.toLowerCase() + "@liferay.com", 0,
					"", LocaleUtil.getDefault(), firstName, "", lastName, 0, 0, true, 8, 15, 1974, job, groupIds,
					new long[] {}, roleIds, new long[] {}, false, serviceContext);
		} catch (UserEmailAddressException.MustNotBeDuplicate ex) {
			user = UserLocalServiceUtil.getUserByEmailAddress(companyId, firstName.toLowerCase() + "@liferay.com");
		}

		assignAddressTo(user, serviceContext);
		setFirstLogin(firstName, user);
		assignRandomFriends(user);

		setupUserProfile(companyId, themeId, guestGroup, user);

		return user;

	}

	private static void setupUserProfile(long companyId, String themeId, Group guestGroup, User user) throws Exception {

		Group group = user.getGroup();
		if (themeId != null && !themeId.isEmpty()) {
			LayoutSetLocalServiceUtil.updateLookAndFeel(group.getGroupId(), false, themeId, "01", "", false);
		}

		Layout layout = addLayout(group, user.getFullName(), false, "/profile", "2_columns_ii");
		JournalArticle cloudArticle, assetListArticle;

		try {
			cloudArticle = JournalArticleLocalServiceUtil.getLatestArticle(guestGroup.getGroupId(),
					SocialDriverConstants.CLOUD_ARTICLE_ID);
		} catch (NoSuchArticleException ex) {
			cloudArticle = addArticle("/cloud-structure.xml", "/cloud-template.vm", "/cloud-article.xml",
					UserLocalServiceUtil.getDefaultUserId(companyId), guestGroup.getGroupId(),
					SocialDriverConstants.CLOUD_ARTICLE_ID);
		}

		try {
			assetListArticle = JournalArticleLocalServiceUtil.getLatestArticle(guestGroup.getGroupId(),
					SocialDriverConstants.ASSETLIST_ARTICLE_ID);
		} catch (NoSuchArticleException ex) {
			assetListArticle = addArticle("/assetlist-structure.xml", "/assetlist-template.vm", "/assetlist-article.xml",
					UserLocalServiceUtil.getDefaultUserId(companyId), guestGroup.getGroupId(),
					SocialDriverConstants.ASSETLIST_ARTICLE_ID);
		}
		try {
			JournalArticleLocalServiceUtil.getLatestArticle(guestGroup.getGroupId(),
					SocialDriverConstants.EXPERTSLIST_ARTICLE_ID);
		} catch (NoSuchArticleException ex) {
			addArticle("/experts-structure.xml", "/experts-template.vm", "/experts-article.xml",
					UserLocalServiceUtil.getDefaultUserId(companyId), guestGroup.getGroupId(),
					SocialDriverConstants.EXPERTSLIST_ARTICLE_ID);
		}

		addPortletId(layout, "com_liferay_social_networking_web_summary_portlet_SummaryPortlet", "column-1");
		addPortletId(layout, "com_liferay_social_requests_web_portlet_SocialRequestsPortlet", "column-1");
		String portletId = addPortletId(layout, JournalContentPortletKeys.JOURNAL_CONTENT, "column-1");
		configureJournalContent(layout, guestGroup, portletId, cloudArticle.getArticleId());
		configurePortletTitle(layout, portletId, "Expertise");
		addPortletBorder(layout, portletId);

		addPortletBorder(layout, addPortletId(layout, "com_liferay_social_networking_web_friends_portlet_FriendsPortlet", "column-1"));

		addPortletBorder(layout, addPortletId(layout, SocialActivitiesPortletKeys.SOCIAL_ACTIVITIES, "column-2"));
		addPortletBorder(layout, addPortletId(layout, "com_liferay_social_networking_web_wall_portlet_WallPortlet", "column-2"));

		// Expertise layout

		layout = addLayout(group, "Expertise", false, "/expertise", "2_columns_ii");

		addPortletId(layout, "com_liferay_social_networking_web_summary_portlet_SummaryPortlet", "column-1");
		addPortletId(layout, "com_liferay_social_requests_web_portlet_SocialRequestsPortlet", "column-1");
		portletId = addPortletId(layout, JournalContentPortletKeys.JOURNAL_CONTENT, "column-1");
		configureJournalContent(layout, guestGroup, portletId, cloudArticle.getArticleId());
		configurePortletTitle(layout, portletId, "Expertise");
		addPortletBorder(layout, portletId);

		portletId = addPortletId(layout, JournalContentPortletKeys.JOURNAL_CONTENT, "column-2");
		configureJournalContent(layout, guestGroup, portletId, assetListArticle.getArticleId());
		configurePortletTitle(layout, portletId, user.getFirstName() + "'s " + "Contributions");
		addPortletBorder(layout, portletId);

		// Social layout

		layout = addLayout(group, "Social", false, "/social", "2_columns_ii");

		addPortletId(layout, "com_liferay_social_networking_web_summary_portlet_SummaryPortlet", "column-1");
		addPortletId(layout, "com_liferay_social_requests_web_portlet_SocialRequestsPortlet", "column-1");
		addPortletBorder(layout, addPortletId(layout, "com_liferay_social_networking_web_friends_portlet_FriendsPortlet", "column-1"));
		addPortletBorder(layout, addPortletId(layout, SocialActivitiesPortletKeys.SOCIAL_ACTIVITIES, "column-2"));
		addPortletBorder(layout, addPortletId(layout, "com_liferay_social_networking_web_wall_portlet_WallPortlet", "column-2"));

		// Blog layout

		layout = addLayout(group, "Blog", false, "/blog", "2_columns_ii");

		addPortletBorder(layout, addPortletId(layout, RecentBloggersPortletKeys.RECENT_BLOGGERS, "column-1"));
		addPortletBorder(layout, addPortletId(layout, PortletKeys.BLOGS, "column-2"));

		// Workspace layout

		layout = addLayout(group, "Workspace", false, "/workspace", "2_columns_i");

		addPortletBorder(layout, addPortletId(layout, PortletKeys.RECENT_DOCUMENTS, "column-1"));
		addPortletBorder(layout, addPortletId(layout, PortletKeys.DOCUMENT_LIBRARY, "column-2"));

		addPortletId(layout, "com_liferay_calendar_web_portlet_CalendarPortlet", "column-2");
	}

	// make a fake social network of friends by randomly friending some
	// people
	private static void assignRandomFriends(User user) throws Exception {

		int userCount = UserLocalServiceUtil.getUsersCount();
		int friendCount = (int) (Math.random() * (double) userCount);
		for (int i = 0; i < friendCount; i++) {
			int frienduser = (int) (Math.random() * (double) userCount);
			User randUser = UserLocalServiceUtil.getUsers(frienduser, frienduser + 1).get(0);
			if (randUser.getUserId() == user.getUserId())
				continue;
			if (randUser.isDefaultUser())
				continue;
			boolean confirm = (Math.random() > .4);
			addSocialRequest(user, randUser, confirm);
		}
	}

	// make it easy to login the first time
	private static void setFirstLogin(String password, User user) throws Exception {

		UserLocalServiceUtil.updatePortrait(user.getUserId(), getPortraitBytes());

		UserLocalServiceUtil.updateLastLogin(user.getUserId(), user.getLoginIP());
		UserLocalServiceUtil.updateAgreedToTermsOfUse(user.getUserId(), true);
		UserLocalServiceUtil.updatePassword(user.getUserId(), password.toLowerCase(), password.toLowerCase(), false,
				true);
		UserLocalServiceUtil.updatePasswordReset(user.getUserId(), false);

		String[] questions = StringUtil.split(PropsUtil.get("users.reminder.queries.questions"));

		String question = questions[0];
		String answer = "1234";

		UserLocalServiceUtil.updateReminderQuery(user.getUserId(), question, answer);
	}

	private static void assignAddressTo(User user, ServiceContext serviceContext) throws SystemException, PortalException {

		String street = "123 Anystreet";
		String city = "Doesnt Matter";
		String zip = "12342";
		List<Country> countrys = CountryServiceUtil.getCountries();
		Country country = countrys.get((int) (Math.random() * (double) countrys.size()));
		List<Region> regions = RegionServiceUtil.getRegions(country.getCountryId());
		long regionId = 0;
		if (regions != null && !regions.isEmpty()) {
			regionId = regions.get((int) (Math.random() * (double) regions.size())).getRegionId();
		}

		AddressLocalServiceUtil.addAddress(user.getUserId(), Contact.class.getName(), user.getContactId(), street, "",
				"", city, zip, regionId, country.getCountryId(), 11002, true, true, serviceContext);
	}

	public static byte[] getBytes(String path) throws Exception {

		return FileUtil.getBytes(getInputStream(path));
	}

	public static InputStream getInputStream(String path) throws Exception {

		BundleContext context = BundleReference.class.cast(
				UserManager.class.getClassLoader()).getBundle()
				.getBundleContext();

		Bundle bundle = context.getBundle();
		InputStream inputStream = bundle.getEntry("/resources" + path).openStream();

		return inputStream;
	}

	public static void setLocalizedValue(Map<Locale, String> map, String value) {

		Locale locale = LocaleUtil.getDefault();

		map.put(locale, value);

		if (!locale.equals(Locale.US)) {
			map.put(Locale.US, value);
		}
	}

	public static String getString(String path) throws Exception {

		return new String(getBytes(path));
	}

	public static JournalArticle addJournalArticle(long userId, long groupId, String articleId, String fileName,
			String ddmStructureKey, String ddmTemplateKey, ServiceContext serviceContext) throws Exception {

		String content = getString(fileName);

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		Map<Locale, String> titleMap = new HashMap<Locale, String>();

		setLocalizedValue(titleMap, articleId);
		try {
			JournalArticleLocalServiceUtil.deleteArticle(groupId, articleId, serviceContext);
		} catch (Exception ex) {
			System.out.println("Ignoring " + ex.getMessage());
		}
		
		return JournalArticleLocalServiceUtil.addArticle(
				userId, groupId, 0, JournalArticleConstants.CLASSNAME_ID_DEFAULT,
				0, articleId, false, 1, titleMap, null, content, ddmStructureKey,
				ddmTemplateKey, null, 1, 1, 2008,
				0, 0, 0, 0, 0, 0, 0,
				true, 0, 0, 0, 0, 0, true, true, false, null, null, null, null,
				serviceContext);
	}

	public static DDMStructure addJournalStructure(long userId, long groupId, String structureId, String fileName)
			throws Exception {

		Map<Locale, String> nameMap = new HashMap<Locale, String>();

		setLocalizedValue(nameMap, structureId);

		Map<Locale, String> descriptionMap = new HashMap<Locale, String>();

		setLocalizedValue(descriptionMap, structureId);

		String xsd = getString(fileName);

		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		try {
			DDMStructure ddmStructure = DDMStructureLocalServiceUtil.getStructure(groupId,
					PortalUtil.getClassNameId(JournalArticle.class), structureId);

			DDMStructureLocalServiceUtil.deleteDDMStructure(ddmStructure);
		} catch (Exception ex) {
			System.out.println("Ignoring " + ex.getMessage());
		}

		DDMXMLUtil.validateXML(xsd);

		DDMForm ddmForm = DDMFormXSDDeserializerUtil.deserialize(xsd);

		DDMFormLayout ddmFormLayout = DDMUtil.getDefaultDDMFormLayout(ddmForm);

		return DDMStructureLocalServiceUtil.addStructure(userId, groupId, StringPool.BLANK,
				PortalUtil.getClassNameId(JournalArticle.class), structureId, nameMap, descriptionMap, ddmForm,
				ddmFormLayout, JournalServiceConfigurationKeys.JOURNAL_ARTICLE_STORAGE_TYPE,
				DDMStructureConstants.TYPE_DEFAULT, serviceContext);

	}

	public static DDMTemplate addJournalTemplate(long userId, long groupId, String templateId, long structureId,
			String fileName) throws Exception {

		Map<Locale, String> descriptionMap = new HashMap<Locale, String>();

		setLocalizedValue(descriptionMap, templateId);

		Map<Locale, String> nameMap = new HashMap<Locale, String>();

		setLocalizedValue(nameMap, templateId);

		String xsl = getString(fileName);

		ServiceContext serviceContext = new ServiceContext();

		try {

			DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.getTemplate(groupId,
					PortalUtil.getClassNameId(DDMStructure.class), templateId);

			DDMTemplateLocalServiceUtil.deleteDDMTemplate(ddmTemplate);

		} catch (Exception ex) {
			System.out.println("Ignoring " + ex.getMessage());

		}
		
		return DDMTemplateLocalServiceUtil.addTemplate(
				userId, groupId, PortalUtil.getClassNameId(DDMStructure.class), 
				structureId, PortalUtil.getClassNameId(JournalArticle.class), templateId,
				nameMap, descriptionMap, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, 
				DDMTemplateConstants.TEMPLATE_MODE_CREATE, "vm", xsl, false, false,
				null, null, serviceContext);
	}

	public static JournalArticle addArticle(String structPath, String tmplPath, String articlePath, long userId, long groupId,
			String articleId) throws Exception {

		DDMStructure struct = addJournalStructure(userId, groupId, articleId, structPath);
		DDMTemplate tmpl = addJournalTemplate(userId, groupId, articleId, struct.getStructureId(), tmplPath);
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		return addJournalArticle(userId, groupId, articleId, articlePath, struct.getStructureKey(),
				tmpl.getTemplateKey(), serviceContext);

	}

	// set a portlet to show its borders
	public static void addPortletBorder(Layout layout, String portletId) throws Exception {

		long ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;
		int ownerType = PortletKeys.PREFS_OWNER_TYPE_LAYOUT;

		PortletPreferences prefs = PortletPreferencesLocalServiceUtil.getPreferences(layout.getCompanyId(), ownerId,
				ownerType, layout.getPlid(), portletId);

		// set desired article id for content display portlet

		prefs.setValue("portletSetupShowBorders", String.valueOf(Boolean.TRUE));

		// update the portlet preferences
		PortletPreferencesLocalServiceUtil.updatePreferences(ownerId, ownerType, layout.getPlid(), portletId, prefs);

	}

	// create a new (blank) page for the given group/s public or private
	// layoutset
	public static Layout addLayout(Group group, String name, boolean privateLayout, String friendlyURL,
			String layoutTemplateId) throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		Layout layout = LayoutLocalServiceUtil.addLayout(group.getCreatorUserId(), group.getGroupId(), privateLayout,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, StringPool.BLANK, StringPool.BLANK,
				LayoutConstants.TYPE_PORTLET, false, friendlyURL, serviceContext);

		LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(0, layoutTemplateId, false);

		// addResources(layout, PortletKeys.DOCKBAR);

		return layout;
	}

	// add a resource to a layout with the portletId. Yes, this comment is
	// completely worthless.
	public void addResources(Layout layout, String portletId) throws Exception {

		String rootPortletId = PortletConstants.getRootPortletId(portletId);

		String portletPrimaryKey = PortletPermissionUtil.getPrimaryKey(layout.getPlid(), portletId);

		ResourceLocalServiceUtil.addResources(layout.getCompanyId(), layout.getGroupId(), 0, rootPortletId,
				portletPrimaryKey, true, true, true);
	}

	// fetch a random portrait from gravatar and return its bytes
	public static byte[] getPortraitBytes() throws Exception {

		String id = SocialDriverConstants.GRAVATAR_IDS[(int) (Math.random()
				* (double) SocialDriverConstants.GRAVATAR_IDS.length)];
		String urlS = "http://2.gravatar.com/avatar/" + id + "?s=80&d=identicon";
		URL url = new URL(urlS);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream in = url.openStream();
		byte[] buf = new byte[2048];
		int bytesRead;
		while ((bytesRead = in.read(buf)) != -1) {
			bos.write(buf, 0, bytesRead);
		}
		in.close();
		return bos.toByteArray();
	}

	// generate a random calendar within the past year
	public static Calendar getCal() {

		Calendar now = Calendar.getInstance();
		int rHours = (int) (Math.random() * 8640);
		now.add(Calendar.HOUR, 0 - rHours);
		return now;
	}

	// remove common tags from the array of passed-in tags
	public String[] removeEnglishStopWords(String[] a) {

		List<String> result = new ArrayList<String>();
		for (String as : a) {
			if (SocialDriverConstants.ENGLISH_STOP_WORDS.contains(as))
				continue;
			if (as.length() < 3)
				continue;
			result.add(as);
		}

		return result.toArray(new String[result.size()]);
	}

	public static void clearUsers(long companyId) throws Exception {

		List<User> users = UserLocalServiceUtil.getCompanyUsers(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		for (User user : users) {
			if (user.isDefaultUser()) {
				System.out.println("skipping default " + user.getScreenName());
				continue;
			}
			if (user.getScreenName().equals("test")) {
				System.out.println("skipping test " + user.getScreenName());
				continue;
			}
			System.out.println("Deleting user " + user.getFullName());
			UserLocalServiceUtil.deleteUser(user.getUserId());
		}

	}
}
