
package com.liferay.social.driver.web.thread;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portlet.social.model.SocialActivityCounter;
import com.liferay.portlet.social.model.SocialActivityCounterConstants;
import com.liferay.portlet.social.service.SocialActivityCounterLocalServiceUtil;
import com.liferay.portlet.social.util.SocialCounterPeriodUtil;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;

import java.util.List;

public class GenerateStats extends Thread {

	private Boolean running = false;
	private long groupId;

	public GenerateStats(long groupId) {

		this.groupId = groupId;
	}

	public void run() {

		while (running) {

			try {
				List<SocialActivityCounter> counters;

				counters =
					SocialActivityCounterLocalServiceUtil.getSocialActivityCounters(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS);
				for (SocialActivityCounter counter : counters) {
					if (!running) {
						break;
					}
					if (counter.getGroupId() == groupId) {
						SocialActivityCounterLocalServiceUtil.deleteSocialActivityCounter(counter.getActivityCounterId());
					}
				}

				long userClassId =
					PortalUtil.getClassNameId(User.class.getName());
				long blogClassId =
					PortalUtil.getClassNameId(BlogsEntry.class.getName());

				long wikiClassId =
					PortalUtil.getClassNameId(WikiPage.class.getName());
				SocialActivityCounter c;

				for (User user : UserLocalServiceUtil.getUsers(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {
					if (!running) {
						break;
					}
					if (user.isDefaultUser())
						continue;
					int pub = 0;
					int puw = 0;
					int pubu = 0;
					int puwu = 0;
					int pump = 0;
					int pua = 0;
					int puc = 0;
					int puv = 0;
					int pp = 0;
					int pc = 0;
					int pcs = 0;
					int pcc = 0;

					for (int off = -12; off <= 0; off++) {
						if (!running) {
							break;
						}
						int startPeriod =
							SocialCounterPeriodUtil.getStartPeriod(off);
						int endPeriod = -1;
						if (off != 0) {
							endPeriod =
								SocialCounterPeriodUtil.getEndPeriod(off);
						}

						int nub = (int) (Math.random() * 12.0);
						int nuw = (int) (Math.random() * 12.0);
						int nubu = (int) (Math.random() * 12.0);
						int nuwu = (int) (Math.random() * 12.0);
						int nump = (int) (Math.random() * 50.0);
						int nua = (int) (Math.random() * 12.0);
						int nuc = (int) (Math.random() * 6.0);
						int nuv = (int) (Math.random() * 52.0);
						int np = (int) (Math.random() * 56.0);
						int nc = (int) (Math.random() * 18.0);
						int ncs = (int) (Math.random() * 22.0);
						int ncc = (int) (Math.random() * 72.0);

						nub =
							(int) ((double) nub * (1.0 + ((double) off / 12.0)));
						nuw =
							(int) ((double) nuw * (1.0 + ((double) off / 12.0)));
						nubu =
							(int) ((double) nubu * (1.0 + ((double) off / 12.0)));
						nuwu =
							(int) ((double) nuwu * (1.0 + ((double) off / 12.0)));
						nump =
							(int) ((double) nump * (1.0 + ((double) off / 12.0)));
						nua =
							(int) ((double) nua * (1.0 + ((double) off / 12.0)));
						nuc =
							(int) ((double) nuc * (1.0 + ((double) off / 12.0)));
						nuv =
							(int) ((double) nuv * (1.0 + ((double) off / 12.0)));
						np =
							(int) ((double) np * (1.0 + ((double) off / 12.0)));
						nc =
							(int) ((double) nc * (1.0 + ((double) off / 12.0)));
						ncs =
							(int) ((double) ncs * (1.0 + ((double) off / 12.0)));
						ncc =
							(int) ((double) ncc * (1.0 + ((double) off / 12.0)));

						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"user.blogs",
								SocialActivityCounterConstants.TYPE_ACTOR,
								pub + nub,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pub + nub);
						c.setCurrentValue(nub);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);

						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"user.wikis",
								SocialActivityCounterConstants.TYPE_ACTOR,
								puw + nuw,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(puw + nuw);
						c.setCurrentValue(nuw);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);

						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"user.blog-updates",
								SocialActivityCounterConstants.TYPE_ACTOR,
								nubu + pubu,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(nubu + pubu);
						c.setCurrentValue(nubu);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"user.wiki-updates",
								SocialActivityCounterConstants.TYPE_ACTOR,
								puwu + nuwu,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(puwu + nuwu);
						c.setCurrentValue(nuwu);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"user.message-posts",
								SocialActivityCounterConstants.TYPE_ACTOR,
								pump + nump,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pump + nump);
						c.setCurrentValue(nump);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"user.activities",
								SocialActivityCounterConstants.TYPE_ACTOR,
								pua + nua,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);

						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pua + nua);
						c.setCurrentValue(nua);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"user.comments",
								SocialActivityCounterConstants.TYPE_ACTOR,
								puc + nuc,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);

						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(puc + nuc);
						c.setCurrentValue(nuc);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"user.votes",
								SocialActivityCounterConstants.TYPE_ACTOR,
								puv + nuv,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);

						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(puv + nuv);
						c.setCurrentValue(nuv);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"participation",
								SocialActivityCounterConstants.TYPE_ACTOR,
								pp + np,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);

						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pp + np);
						c.setCurrentValue(np);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"contribution",
								SocialActivityCounterConstants.TYPE_CREATOR,
								pc + nc,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pc + nc);
						c.setCurrentValue(nc);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"creator.subscriptions",
								SocialActivityCounterConstants.TYPE_CREATOR,
								pcs + ncs,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pcs + ncs);
						c.setCurrentValue(ncs);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								userClassId,
								user.getUserId(),
								"creator.comments",
								SocialActivityCounterConstants.TYPE_CREATOR,
								pcc + ncc,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pcc + ncc);
						c.setCurrentValue(ncc);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);

						pub += nub;
						puw += nuw;
						pubu += nubu;
						puwu += nuwu;
						pump += nump;
						pua += nua;
						puc += nuc;
						puv += nuv;
						pp += np;
						pc += nc;
						pcs += ncs;
						pcc += ncc;

					}
				}

				for (BlogsEntry blog : BlogsEntryLocalServiceUtil.getBlogsEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

					if (!running) {
						break;
					}

					int paa = 0;
					int pac = 0;
					int par = 0;
					int pas = 0;
					int pp = 0;
					int pav = 0;

					for (int off = -12; off <= 0; off++) {

						if (!running) {
							break;
						}

						int naa = (int) (Math.random() * 19.0);
						int nac = (int) (Math.random() * 19.0);
						int nar = (int) (Math.random() * 19.0);
						int nas = (int) (Math.random() * 14.0);
						int np = (int) (Math.random() * 14.0);
						int nav = (int) (Math.random() * 14.0);

						naa =
							(int) ((double) naa * (1.0 + ((double) off / 12.0)));
						nac =
							(int) ((double) nac * (1.0 + ((double) off / 12.0)));
						nar =
							(int) ((double) nar * (1.0 + ((double) off / 12.0)));
						nas =
							(int) ((double) nas * (1.0 + ((double) off / 12.0)));
						np =
							(int) ((double) np * (1.0 + ((double) off / 12.0)));
						nav =
							(int) ((double) nav * (1.0 + ((double) off / 12.0)));

						int startPeriod =
							SocialCounterPeriodUtil.getStartPeriod(off);
						int endPeriod = -1;
						if (off != 0) {
							endPeriod =
								SocialCounterPeriodUtil.getEndPeriod(off);
						}

						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								blogClassId,
								blog.getEntryId(),
								"asset.activities",
								SocialActivityCounterConstants.TYPE_ASSET,
								naa + paa,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);

						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(naa + paa);
						c.setCurrentValue(naa);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);

						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								blogClassId,
								blog.getEntryId(),
								"asset.comments",
								SocialActivityCounterConstants.TYPE_ASSET,
								nac + pac,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(nac + pac);
						c.setCurrentValue(nac);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								blogClassId,
								blog.getEntryId(),
								"asset.replies",
								SocialActivityCounterConstants.TYPE_ASSET,
								nar + par,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(nar + par);
						c.setCurrentValue(nar);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								blogClassId,
								blog.getEntryId(),
								"asset.subscriptions",
								SocialActivityCounterConstants.TYPE_ASSET,
								pas + nas,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pas + nas);
						c.setCurrentValue(nas);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);

						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								blogClassId,
								blog.getEntryId(),
								"popularity",
								SocialActivityCounterConstants.TYPE_ASSET,
								pp + np,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pp + np);
						c.setCurrentValue(np);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);

						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								blogClassId,
								blog.getEntryId(),
								"asset.votes",
								SocialActivityCounterConstants.TYPE_ASSET,
								pav + nav,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pav + nav);
						c.setCurrentValue(nav);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						paa += naa;
						pac += nac;
						par += nar;
						pas += nas;
						pp += np;
						pav += nav;

					}
				}

				for (WikiPage wiki : WikiPageLocalServiceUtil.getWikiPages(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

					if (!running) {
						break;
					}

					int paa = 0;
					int pac = 0;
					int pas = 0;
					int pp = 0;
					int pav = 0;
					for (int off = -12; off <= 0; off++) {

						if (!running) {
							break;
						}

						int naa = (int) (Math.random() * 19.0);
						int nac = (int) (Math.random() * 19.0);
						int nas = (int) (Math.random() * 14.0);
						int np = (int) (Math.random() * 14.0);
						int nav = (int) (Math.random() * 14.0);

						naa =
							(int) ((double) naa * (1.0 + ((double) off / 12.0)));
						nac =
							(int) ((double) nac * (1.0 + ((double) off / 12.0)));
						nas =
							(int) ((double) nas * (1.0 + ((double) off / 12.0)));
						np =
							(int) ((double) np * (1.0 + ((double) off / 12.0)));
						nav =
							(int) ((double) nav * (1.0 + ((double) off / 12.0)));

						int startPeriod =
							SocialCounterPeriodUtil.getStartPeriod(off);
						int endPeriod = -1;
						if (off != 0) {
							endPeriod =
								SocialCounterPeriodUtil.getEndPeriod(off);
						}
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								wikiClassId,
								wiki.getPrimaryKey(),
								"asset.activities",
								SocialActivityCounterConstants.TYPE_ASSET,
								naa + paa,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(naa + paa);
						c.setCurrentValue(naa);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								wikiClassId,
								wiki.getPrimaryKey(),
								"asset.comments",
								SocialActivityCounterConstants.TYPE_ASSET,
								pac + nac,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pac + nac);
						c.setCurrentValue(nac);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								wikiClassId,
								wiki.getPrimaryKey(),
								"asset.subscriptions",
								SocialActivityCounterConstants.TYPE_ASSET,
								pas + nas,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pas + nas);
						c.setCurrentValue(nas);

						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								wikiClassId,
								wiki.getPrimaryKey(),
								"popularity",
								SocialActivityCounterConstants.TYPE_ASSET,
								pp + np,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pp + np);
						c.setCurrentValue(np);
						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						c =
							SocialActivityCounterLocalServiceUtil.addActivityCounter(
								groupId,
								wikiClassId,
								wiki.getPrimaryKey(),
								"asset.votes",
								SocialActivityCounterConstants.TYPE_ASSET,
								pav + nav,
								0,
								SocialActivityCounterConstants.PERIOD_LENGTH_SYSTEM);
						c.setStartPeriod(startPeriod);
						c.setEndPeriod(endPeriod);
						c.setTotalValue(pav + nav);
						c.setCurrentValue(nav);

						SocialActivityCounterLocalServiceUtil.updateSocialActivityCounter(c);
						paa += naa;
						pac += nac;
						pas += nas;
						pp += np;
						pav += nav;

					}
				}
				running = false;
				System.out.println("Generate Stats Stopped");
			}
			catch (SystemException | PortalException e) {
				e.printStackTrace();
			}
		}
	}

	public void startThread() {

		running = true;
		start();
	}

	public void stopThread() {

		running = false;
	}

	public boolean isRunning() {

		return running;
	}

}
