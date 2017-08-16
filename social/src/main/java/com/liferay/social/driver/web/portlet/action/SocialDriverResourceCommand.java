package com.liferay.social.driver.web.portlet.action;

import com.liferay.blogs.kernel.service.BlogsEntryLocalService;
import com.liferay.message.boards.kernel.service.MBCategoryLocalService;
import com.liferay.message.boards.kernel.service.MBDiscussionLocalService;
import com.liferay.message.boards.kernel.service.MBMessageLocalService;
import com.liferay.message.boards.kernel.service.MBThreadLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.ratings.kernel.service.RatingsEntryService;
import com.liferay.social.driver.web.constants.SocialDriverPortletKeys;
import com.liferay.social.driver.web.content.ContentGenerator;
import com.liferay.social.driver.web.thread.CreateContent;
import com.liferay.social.driver.web.thread.GenerateStats;
import com.liferay.social.kernel.service.SocialRequestLocalService;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + SocialDriverPortletKeys.SOCIAL_DRIVER,
		"mvc.command.name=/social_driver/run"
	},
	service = MVCResourceCommand.class
)

public class SocialDriverResourceCommand implements MVCResourceCommand {

	private GenerateStats statsThread = null;
	private CreateContent createThread = null;
	long companyId = 0;
	long groupId = 0;

	@Deactivate
	protected void deactivate() {

		stopAll();
	}

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		HttpServletRequest request = PortalUtil
				.getOriginalServletRequest(PortalUtil.getHttpServletRequest(resourceRequest));

		companyId = PortalUtil.getCompanyId(request);
		try {
			groupId = PortalUtil.getScopeGroupId(request);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}

		String action = ParamUtil.getString(request, "action");

		if (action.contains("startContent")) {
			startContent(request);
		} else if (action.contains("genStats")) {
			genStats();
		} else if (action.contains("stopAll")) {
			stopAll();
		}
		return true;
	}

	public void startContent(HttpServletRequest request) {

		final ContentGenerator contentContainer = new ContentGenerator();
		String themeId = ParamUtil.getString(request, "themeId");

		if (createThread == null) {
			stopAll();
			System.out.println("Starting Content Generator");

			createThread = new CreateContent(companyId, groupId, themeId, contentContainer);
			createThread.startThread();
		}
	}

	private void genStats() {

		if (statsThread == null) {
			stopAll();
			System.out.println("Starting Generating Social Stats");
			statsThread = new GenerateStats(groupId);
			statsThread.startThread();
		}
	}

	private void stopAll() {

		if (createThread != null) {
			System.out.println("Stopping All Generators");
			createThread.stopThread();
			createThread = null;
		} else if (statsThread != null) {
			System.out.println("Stopping All Generators");
			statsThread.stopThread();
			statsThread = null;
		}
	}

	@Reference
	public void setAddressLocalService(AddressLocalService addressLocalService) {
		_addressLocalService = addressLocalService;
	}

	@Reference
	public void setBlogsEntryLocalService(BlogsEntryLocalService blogsEntryLocalService) {

		_blogsEntryLocalService = blogsEntryLocalService;
	}

	@Reference
	public void setCountryService(CountryService countryService) {
		_countryService = countryService;
	}

	@Reference
	public void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Reference
	public void setLayoutLocalService(LayoutLocalService layoutLocalService) {
		_layoutLocalService = layoutLocalService;
	}

	@Reference
	public void setRegionService(RegionService regionService) {
		_regionService = regionService;
	}

	@Reference
	public void setResourceLocalService(ResourceLocalService resourceLocalService) {
		_resourceLocalService = resourceLocalService;
	}

	@Reference
	public void setRoleLocalService(RoleLocalService roleLocalService) {
		_roleLocalService = roleLocalService;
	}

	@Reference
	public void setSocialRequestLocalService(SocialRequestLocalService socialRequestLocalService) {
		_socialRequestLocalService = socialRequestLocalService;
	}

	@Reference
	public void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	@Reference
	public void setWikiNodeLocalService(WikiNodeLocalService wikiNodeLocalService) {
		_wikiNodeLocalService = wikiNodeLocalService;
	}

	@Reference
	public void setWikiPageLocalService(WikiPageLocalService wikiPageLocalService) {
		_wikiPageLocalService = wikiPageLocalService;
	}

	@Reference
	public void setMBCategoryLocalService(

			MBCategoryLocalService mbCategoryLocalService) {
		_mbCategoryLocalService = mbCategoryLocalService;
	}
	
	@Reference
	public void setMBDiscussionLocalService(

			MBDiscussionLocalService mbDiscussionLocalService) {
		_mbDiscussionLocalService = mbDiscussionLocalService;
	}

	@Reference
	public void setMBThreadLocalService(

			MBThreadLocalService mbThreadLocalService) {
		_mbThreadLocalService = mbThreadLocalService;
	}

	@Reference
	public void setMBMessageLocalService(

			MBMessageLocalService mbMessageLocalService) {
		_mbMessageLocalService = mbMessageLocalService;
	}

	@Reference
	public void setRatingsEntryService(

			RatingsEntryService ratingsEntryService) {
		_ratingsEntryService = ratingsEntryService;
	}

	public static AddressLocalService _addressLocalService;
	public static BlogsEntryLocalService _blogsEntryLocalService;
	public static CountryService _countryService;
	public static GroupLocalService _groupLocalService;
	public static LayoutLocalService _layoutLocalService;
	public static MBCategoryLocalService _mbCategoryLocalService;
	public static MBDiscussionLocalService _mbDiscussionLocalService;
	public static MBMessageLocalService _mbMessageLocalService;
	public static MBThreadLocalService _mbThreadLocalService;
	public static RatingsEntryService _ratingsEntryService;
	public static RegionService _regionService;
	public static ResourceLocalService _resourceLocalService;
	public static RoleLocalService _roleLocalService;
	public static SocialRequestLocalService _socialRequestLocalService;
	public static UserLocalService _userLocalService;
	public static WikiNodeLocalService _wikiNodeLocalService;
	public static WikiPageLocalService _wikiPageLocalService;
}
