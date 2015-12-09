
package com.liferay.social.driver.web.content;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.asset.DuplicateTagException;
import com.liferay.portlet.messageboards.model.MBCategory;
import com.liferay.portlet.messageboards.model.MBCategoryConstants;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.social.driver.web.portlet.action.SocialDriverResourceCommand;

public class MBManager {

	private static String themeId;
	private static long companyId;
	private static long groupId;
	private static ContentGenerator contentContainer;

	public MBManager(
		long companyId, long groupId, String themeId,
		ContentGenerator contentContainer) {

		MBManager.companyId = companyId;
		MBManager.groupId = groupId;
		MBManager.themeId = themeId;
		MBManager.contentContainer = contentContainer;
	}

	public static void replyMsg()
		throws Exception {

		Calendar rCal = UserManager.getCal();

		ServiceContext context = new ServiceContext();
		context.setCreateDate(rCal.getTime());
		context.setModifiedDate(rCal.getTime());
		String cid = contentContainer.getRandomId();
		String content = contentContainer.getContentBody(cid);
		String[] tags = contentContainer.getContentTags(cid);
		context.setAssetTagNames(tags);
		context.setCompanyId(companyId);
		context.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		context.setAddGroupPermissions(true);
		context.setAddGuestPermissions(true);

		context.setScopeGroupId(groupId);

		if (SocialDriverResourceCommand._mbMessageLocalService.getGroupMessagesCount(
			groupId, WorkflowConstants.STATUS_APPROVED) <= 0) {
			return;
		}

		int rand =
			(int) (Math.random() * (double) SocialDriverResourceCommand._mbMessageLocalService.getGroupMessagesCount(
				groupId, WorkflowConstants.STATUS_APPROVED));
		MBMessage msg =
				SocialDriverResourceCommand._mbMessageLocalService.getGroupMessages(
				groupId, WorkflowConstants.STATUS_APPROVED, rand, rand + 1).get(
				0);
		long categoryId = msg.getCategoryId();
		long threadId = msg.getThreadId();
		long parentId = msg.getMessageId();
		
		try {
			
			SocialDriverResourceCommand._mbMessageLocalService.addMessage(
				UserManager.getUserId(companyId, themeId),
				"Joe Schmoe", groupId, categoryId, threadId, parentId,
				"RE: " + msg.getSubject(), content, "html",
				new ArrayList<ObjectValuePair<String, InputStream>>(), false, 1.0,
				true, context);
		} catch (DuplicateTagException ex) {
			
		}

		// System.out.println("replied to msg " + msg.getSubject() + " in " +
		// "category " + msg.getCategory().getName());
	}

	public static void addMsg()
		throws Exception {

		Calendar rCal = UserManager.getCal();

		long userId = UserManager.getUserId(companyId, themeId);
		String contentId = contentContainer.getRandomId();
		String title = contentContainer.getContentTitle(contentId);
		String content = contentContainer.getContentBody(contentId);
		String[] tags = contentContainer.getContentTags(contentId);

		ServiceContext context = new ServiceContext();
		context.setCreateDate(rCal.getTime());
		context.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		context.setModifiedDate(rCal.getTime());
		context.setAssetTagNames(tags);
		context.setAddGroupPermissions(true);
		context.setAddGuestPermissions(true);

		context.setCompanyId(companyId);
		context.setScopeGroupId(groupId);

		MBCategory newcat =
				SocialDriverResourceCommand._mbCategoryLocalService.addCategory(
				userId, 0, StringUtil.shorten(title, 70), "Discussions about " +
					title, MBCategoryConstants.DEFAULT_DISPLAY_STYLE, null,
				null, null, 0, false, null, null, -1, null, false, null, 0,
				false, null, null, false, false, context);

		try {
			SocialDriverResourceCommand._mbMessageLocalService.addMessage(
				UserManager.getUserId(companyId, themeId),
				"Hoe Schmoe", groupId, newcat.getCategoryId(), 0, 0, title,
				content, "html",
				new ArrayList<ObjectValuePair<String, InputStream>>(), false, 1.0,
				true, context);
			
		} catch (DuplicateTagException ex) {
			
		}
		// System.out.println("added msg " + title + " in category " + newcat
		// .getName());

	}
}
