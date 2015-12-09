
package com.liferay.social.driver.web.content;

import java.math.BigInteger;
import java.util.Calendar;

import com.liferay.portal.kernel.security.SecureRandom;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.asset.DuplicateTagException;
import com.liferay.portlet.messageboards.model.MBDiscussion;
import com.liferay.portlet.ratings.service.RatingsEntryServiceUtil;
import com.liferay.social.driver.web.portlet.action.SocialDriverResourceCommand;
import com.liferay.wiki.exception.DuplicatePageException;
import com.liferay.wiki.exception.PageTitleException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;

public class WikiManager {

	private static String themeId;
	private static long companyId;
	private static long groupId;
	private static ContentGenerator contentContainer;

	public WikiManager(
		long companyId, long groupId, String themeId,
		ContentGenerator contentContainer) {

		WikiManager.companyId = companyId;
		WikiManager.groupId = groupId;
		WikiManager.themeId = themeId;
		WikiManager.contentContainer = contentContainer;
	}

	private static SecureRandom random = new SecureRandom();

	private static String randomTitle(String title) {

		return title + new BigInteger(130, random).toString(32);
	}

	public static void addEntry()
		throws Exception {

		Calendar rCal = UserManager.getCal();

		ServiceContext context = new ServiceContext();
		context.setCreateDate(rCal.getTime());
		context.setModifiedDate(rCal.getTime());
		String cid = contentContainer.getRandomId();
		String title = randomTitle(contentContainer.getContentTitle(cid));
		String content = contentContainer.getContentBody(cid);
		String[] tags = contentContainer.getContentTags(cid);
		context.setAssetTagNames(tags);
		context.setCompanyId(companyId);
		context.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		context.setAddGroupPermissions(true);
		context.setAddGuestPermissions(true);

		context.setScopeGroupId(groupId);

		WikiNode def;

		long defId = UserLocalServiceUtil.getDefaultUserId(companyId);
		try {
			def = SocialDriverResourceCommand._wikiNodeLocalService.getNode(groupId, "Main");
			// System.out.println("got main node for group " + groupId + ": " +
			// def);
		}
		catch (Exception ex) {
			def = SocialDriverResourceCommand._wikiNodeLocalService.addDefaultNode(defId, context);
			// System.out.println("created main node for group " + groupId +
			// ": " +
			// "" + def);
		}

		try {
			// System.out.println("Adding Wiki " + title + "node:" + def + " " +
			// "tags: " + Arrays.asList(tags));
			SocialDriverResourceCommand._wikiPageLocalService.addPage(
				UserManager.getUserId(companyId, themeId),
				def.getNodeId(), title, content, "", false, context);

		}
		catch (DuplicatePageException ignored) {

		}
		catch (PageTitleException ignored) {

		}
		
		catch (DuplicateTagException ignored) {

		}
	}

	public static void commentEntry()
		throws Exception {

		
		try {
		if (SocialDriverResourceCommand._wikiPageLocalService.getWikiPagesCount() <= 0)
			return;
		}
		catch (ExceptionInInitializerError ex) {
			ex.getCause();
		}
		
		int rand =
			(int) (Math.random() * (double) SocialDriverResourceCommand._wikiPageLocalService.getWikiPagesCount());
		WikiPage entry =
				SocialDriverResourceCommand._wikiPageLocalService.getWikiPages(rand, rand + 1).get(0);
		Calendar rCal = UserManager.getCal();

		long userId = UserManager.getUserId(companyId, themeId);
		ServiceContext context = new ServiceContext();
		context.setCreateDate(rCal.getTime());
		context.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		context.setModifiedDate(rCal.getTime());
		context.setCompanyId(companyId);
		context.setScopeGroupId(groupId);
		context.setAddGroupPermissions(true);
		context.setAddGuestPermissions(true);

		// System.out.println("Commenting on wiki \"" + entry.getTitle() +
		// "\"");

		MBDiscussion disc =
				SocialDriverResourceCommand._mbDiscussionLocalService.getDiscussion(
				WikiPage.class.getName(), entry.getResourcePrimKey());
		SocialDriverResourceCommand._mbMessageLocalService.addDiscussionMessage(
			userId,
			"Joe Schmoe",
			context.getScopeGroupId(),
			WikiPage.class.getName(),
			entry.getResourcePrimKey(),
			disc.getThreadId(),
			SocialDriverResourceCommand._mbThreadLocalService.getMBThread(disc.getThreadId()).getRootMessageId(),
			"Subject of comment", "This is great", context);
	}

	public static void voteEntry()
		throws Exception {

		try {
		if (SocialDriverResourceCommand._wikiPageLocalService.getWikiPagesCount() <= 0)
			return;
		}
		catch (ExceptionInInitializerError ex) {
			ex.getCause();
		}
		int rand =
			(int) (Math.random() * (double) SocialDriverResourceCommand._wikiPageLocalService.getWikiPagesCount());
		WikiPage entry =
				SocialDriverResourceCommand._wikiPageLocalService.getWikiPages(rand, rand + 1).get(0);
		Calendar rCal = UserManager.getCal();

		long userId = UserManager.getUserId(companyId, themeId);
		ServiceContext context = new ServiceContext();
		context.setCreateDate(rCal.getTime());
		context.setModifiedDate(rCal.getTime());
		context.setCompanyId(companyId);
		context.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		context.setAddGroupPermissions(true);
		context.setAddGuestPermissions(true);

		context.setScopeGroupId(groupId);

		PrincipalThreadLocal.setName(userId);
		RatingsEntryServiceUtil.updateEntry(
			WikiPage.class.getName(), entry.getResourcePrimKey(),
			(int) (Math.random() * 0.0) + 1);

		// System.out.println("Voted on Wiki entry " + entry.getTitle());
	}
}