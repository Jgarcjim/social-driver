
package com.liferay.social.driver.web.content;

import java.util.Calendar;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.asset.DuplicateTagException;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.messageboards.model.MBDiscussion;
import com.liferay.portlet.ratings.service.RatingsEntryServiceUtil;
import com.liferay.social.driver.web.portlet.action.SocialDriverResourceCommand;

public class BlogManager {

	private static String themeId;
	private static long companyId;
	private static long groupId;
	private static ContentGenerator contentContainer;

	public BlogManager(long companyId, long groupId, String themeId, 
			ContentGenerator contentContainer) {

		BlogManager.companyId = companyId;
		BlogManager.groupId = groupId;
		BlogManager.themeId = themeId;
		BlogManager.contentContainer = contentContainer;
	}

	public static void addEntry()
		throws Exception {

		Calendar rCal = UserManager.getCal();

		ServiceContext context = new ServiceContext();
		context.setCreateDate(rCal.getTime());
		context.setModifiedDate(rCal.getTime());
		String cid = contentContainer.getRandomId();
		String title = contentContainer.getContentTitle(cid);
		String content = contentContainer.getContentBody(cid);
		String[] tags = contentContainer.getContentTags(cid);
		context.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		context.setAddGroupPermissions(true);
		context.setAddGuestPermissions(true);

		context.setAssetTagNames(tags);
		context.setCompanyId(companyId);
		context.setScopeGroupId(groupId);
		
		try {

		SocialDriverResourceCommand._blogsEntryLocalService.addEntry(
			UserManager.getUserId(companyId, themeId), title, StringPool.BLANK, "", 
			content, rCal.get(Calendar.MONTH), rCal.get(Calendar.DAY_OF_MONTH),
			rCal.get(Calendar.YEAR), rCal.get(Calendar.HOUR_OF_DAY),
			rCal.get(Calendar.MINUTE), false, false, null, 
			null, null, 
			null, context);
		} catch (DuplicateTagException ex) {
			
		}

		// System.out.println("Added blog " + newEntry.getTitle() + " Tags: " +
		// Arrays.asList(tags));
	}

	public static void commentEntry() throws Exception {

		if (SocialDriverResourceCommand._blogsEntryLocalService.getBlogsEntriesCount() <= 0)
			return;
		int rand = (int) (Math.random() * (double) SocialDriverResourceCommand._blogsEntryLocalService.getBlogsEntriesCount());
		BlogsEntry entry = SocialDriverResourceCommand._blogsEntryLocalService.getBlogsEntries(rand, rand + 1).get(0);
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

		MBDiscussion disc = SocialDriverResourceCommand._mbDiscussionLocalService.getDiscussion(BlogsEntry.class.getName(),
				entry.getPrimaryKey());
		SocialDriverResourceCommand._mbMessageLocalService.addDiscussionMessage(userId, "Joe Schmoe", context.getScopeGroupId(),
				BlogsEntry.class.getName(), entry.getPrimaryKey(), disc.getThreadId(),
				SocialDriverResourceCommand._mbThreadLocalService.getMBThread(disc.getThreadId()).getRootMessageId(), "Subject of comment",
				"This is great", context);

		// System.out.println("Commented on Blog " + entry.getTitle());
	}

	public static void voteEntry() throws Exception {

		if (SocialDriverResourceCommand._blogsEntryLocalService.getBlogsEntriesCount() <= 0)
			return;
		int rand = (int) (Math.random() * (double) SocialDriverResourceCommand._blogsEntryLocalService.getBlogsEntriesCount());
		BlogsEntry entry = SocialDriverResourceCommand._blogsEntryLocalService.getBlogsEntries(rand, rand + 1).get(0);
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
		RatingsEntryServiceUtil.updateEntry(BlogsEntry.class.getName(), entry.getEntryId(),
				(int) (Math.random() * 0.0) + 1);
		// System.out.println("Voted on Blog " + entry.getTitle());
	}
}
