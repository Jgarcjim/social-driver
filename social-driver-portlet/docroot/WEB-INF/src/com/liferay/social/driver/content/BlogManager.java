
package com.liferay.social.driver.content;

import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portlet.messageboards.model.MBDiscussion;
import com.liferay.portlet.messageboards.service.MBDiscussionLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBThreadLocalServiceUtil;
import com.liferay.portlet.ratings.service.RatingsEntryServiceUtil;

import java.util.Calendar;

public class BlogManager {

	private static String themeId;
	private static long companyId;
	private static long groupId;
	private static ContentGenerator contentContainer = new ContentGenerator();

	public BlogManager(
		long companyId, long groupId, String themeId,
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

		BlogsEntryLocalServiceUtil.addEntry(
			UserManager.getUserId(companyId, themeId), title, "",
			content, rCal.get(Calendar.MONTH), rCal.get(Calendar.DAY_OF_MONTH),
			rCal.get(Calendar.YEAR), rCal.get(Calendar.HOUR_OF_DAY),
			rCal.get(Calendar.MINUTE), false, false, null, false, null, null,
			null, context);

		// System.out.println("Added blog " + newEntry.getTitle() + " Tags: " +
		// Arrays.asList(tags));
	}

	public static void commentEntry()
		throws Exception {

		if (BlogsEntryLocalServiceUtil.getBlogsEntriesCount() <= 0)
			return;
		int rand =
			(int) (Math.random() * (double) BlogsEntryLocalServiceUtil.getBlogsEntriesCount());
		BlogsEntry entry =
			BlogsEntryLocalServiceUtil.getBlogsEntries(rand, rand + 1).get(0);
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

		MBDiscussion disc =
			MBDiscussionLocalServiceUtil.getDiscussion(
				BlogsEntry.class.getName(), entry.getPrimaryKey());
		MBMessageLocalServiceUtil.addDiscussionMessage(
			userId,
			"Joe Schmoe",
			context.getScopeGroupId(),
			BlogsEntry.class.getName(),
			entry.getPrimaryKey(),
			disc.getThreadId(),
			MBThreadLocalServiceUtil.getMBThread(disc.getThreadId()).getRootMessageId(),
			"Subject of comment", "This is great", context);

		// System.out.println("Commented on Blog " + entry.getTitle());
	}

	public static void voteEntry()
		throws Exception {

		if (BlogsEntryLocalServiceUtil.getBlogsEntriesCount() <= 0)
			return;
		int rand =
			(int) (Math.random() * (double) BlogsEntryLocalServiceUtil.getBlogsEntriesCount());
		BlogsEntry entry =
			BlogsEntryLocalServiceUtil.getBlogsEntries(rand, rand + 1).get(0);
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
			BlogsEntry.class.getName(), entry.getEntryId(),
			(int) (Math.random() * 5.0) + 1);
		// System.out.println("Voted on Blog " + entry.getTitle());
	}

}
