package jp.gr.java_conf.ya.yumura.String; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved. --><!-- This software includes the work that is distributed in the Apache License 2.0

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gr.java_conf.ya.yumura.Twitter.TwitterAccess;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

public class ViewString {
    // public static final SimpleDateFormat sdf_yyyyMMddHHmmssOnlyNumber = new SimpleDateFormat("yyyyMMddHHmmss", Locale.JAPAN);
    // public static final SimpleDateFormat sdf_yyyyMMddHHmmssSSSOnlyNumber = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.JAPAN);
    public static final SimpleDateFormat sdf_yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.JAPAN);
    private static boolean pref_debug_write_logcat = false;

    public static String getScreennameAndText(final Status status) {
        final StringBuilder sb = new StringBuilder();
        if (pref_debug_write_logcat)
            Log.i("Yumura", "getScreennameAndText() sb");
        try {
            sb.append("@");
            sb.append(status.getUser().getScreenName());
            sb.append(": ");
            sb.append(status.getText());
        } catch (Exception e) {
            if (pref_debug_write_logcat) Log.e("Yumura", e.getMessage());
        }
        return sb.toString();
    }

    public static String getScreennameAndTextAndFooter(Status status) {
        status = getOriginalStatus(status);
        final StringBuilder sb = new StringBuilder();
        try {
            sb.append("@");
            sb.append(status.getUser().getScreenName());
            sb.append(": ");
            sb.append(status.getText());
            sb.append(" ");
            sb.append(getTweetFooter(status));
        } catch (Exception e) {
            if (pref_debug_write_logcat) Log.e("Yumura", e.getMessage());
        }
        return sb.toString();
    }

    public static String getStatusText(Status status, final boolean pref_tl_img_show) {
        status = getOriginalStatus(status);
        final StringBuilder sb = new StringBuilder();
        try {
            sb.append("@").append(status.getUser().getScreenName()).append(" <br>");
            sb.append(getTextExpanded(status, pref_tl_img_show)).append("<br>");
            sb.append(getTweetFooter(status));

            if (status.isFavorited())
                sb.append("<img src=\"favorite_on\">");

            if (status.isRetweetedByMe()) {
                sb.append("<img src=\"retweet_on\">");
            } else if (status.isRetweet()) {
                sb.append("<img src=\"retweet_hover\">");
            }
        } catch (Exception e) {
            if (pref_debug_write_logcat) Log.e("Yumura", e.getMessage());
        }
        return sb.toString();
    }

    public static String getTextExpanded(final Status status, final boolean pref_tl_img_show) {
        String text = status.getText();

        text = getTextExpandedTco(text, status);
        if (pref_tl_img_show)
            text = getTextExpandedImg(text, status);

        return text;
    }

    public static String getTextExpandedImg(String text, final Status status) {
        MediaEntity[] extendedMediaEntities = status.getExtendedMediaEntities();
        for (int i = 0; i < extendedMediaEntities.length; i++) {
            int pSizeKey = MediaEntity.Size.LARGE + 1;
            MediaEntity.Size pSize = null;
            String pMediaUrl = "";

            final MediaEntity mediaEntity = extendedMediaEntities[i];
            final Map<Integer, MediaEntity.Size> sizes = mediaEntity.getSizes();
            for (Map.Entry<Integer, MediaEntity.Size> e : sizes.entrySet()) {
                final Integer sizeKey = e.getKey();
                if (sizeKey < pSizeKey) {
                    pSize = e.getValue();
                    pMediaUrl = getSizeMediaURL(mediaEntity.getMediaURL(), getSizeString(sizeKey));
                }
            }

            final String link = getLinkedImg(mediaEntity.getExpandedURL(), pMediaUrl, pSize, mediaEntity.getDisplayURL());
            text = text + " " + link;
        }

        return text;
    }

    public static String getTextExpandedTco(String text, final Status status) {
        final URLEntity[] entities = status.getURLEntities();
        if ((entities != null) && (entities.length > 0)) {
            for (URLEntity entity : entities) {
                final String link = getLinkedUrl(entity);
                final String tco = entity.getURL();
                final Pattern p = Pattern.compile(tco);
                final Matcher m = p.matcher(text);
                text = m.replaceAll(link);
            }
        }

        return text;
    }

    public static String getLinkedImg(final String href, final String src, final MediaEntity.Size size, final String displayUrl) {
        return "<a href=\"" + href + "\">" + displayUrl + "<img src=\"" + src + "\" width=\"" + Integer.toString(size.getWidth()) + "\" height=\"" + Integer.toString(size.getHeight()) + "\" ></a>";
    }

    public static String getLinkedUrl(final URLEntity entity) {
        return "<a href=\"" + entity.getExpandedURL() + "\">" + entity.getExpandedURL() + "</a>";
    }

    private static Status getOriginalStatus(final Status status) {
        try {
            return (status.getRetweetedStatus() != null) ? status.getRetweetedStatus() : status;
        } catch (Exception e) {
            if (pref_debug_write_logcat) Log.e("Yumura", e.getMessage());
            return status;
        }
    }

    public static String getParmaLink(final Status status) {
        return TwitterAccess.URL_PROTOCOL + TwitterAccess.URL_TWITTER + "/" + status.getUser().getScreenName() + "/status/" + status.getId();
    }

    private static String getSizeMediaURL(final String mediaUrl, final String sizeString) {
        return mediaUrl + ":" + sizeString;
    }

    private static String getSizeString(final Integer sizeKey) {
        if (MediaEntity.Size.LARGE.equals(sizeKey)) {
            return "large";
        } else if (MediaEntity.Size.MEDIUM.equals(sizeKey)) {
            return "medium";
        } else if (MediaEntity.Size.SMALL.equals(sizeKey)) {
            return "small";
        } else if (MediaEntity.Size.THUMB.equals(sizeKey)) {
            return "thumb";
        } else {
            return "";
        }
    }

    public static String getTweetFooter(final Status status) {
        final StringBuilder sb = new StringBuilder();

        try {
            sb.append(sdf_yyyyMMddHHmmssSSS.format(status.getCreatedAt())).append(" ");
            if (status.getRetweetCount() > 0)
                sb.append(status.getRetweetCount()).append("RT ");
            if (status.getFavoriteCount() > 0)
                sb.append(status.getFavoriteCount()).append("Fav ");
            sb.append(status.getSource().replaceAll("<[^>]+>", ""));
        } catch (Exception e) {
            if (pref_debug_write_logcat) Log.e("Yumura", e.getMessage());
        }
        return sb.toString();
    }
}
