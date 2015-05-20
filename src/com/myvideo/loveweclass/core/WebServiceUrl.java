package com.myvideo.loveweclass.core;

public class WebServiceUrl {
	public static final String CHANNELS_URL = "http://api.ifood.tv/4.0/channels?auth-token=1212551&site-id=1&device-id=";
	public static final String SEARCH_URL = "http://api.ifood.tv/4.0/search?site-id=1&query=";
	public static final String DETAIL_URL = "http://api.ifood.tv/1.0/doc/get-info?id=";
	public static final String AUTH_TOKEN = "&auth-token=1212551";
	public static final String SEARCH_PARAM = "&max-results=50" + AUTH_TOKEN;
	public static final String CATEGORY_URL = "http://api.ifood.tv/4.0/category_map?auth-token=1212551&site-id=1";
	public static final String IFOODTV_URL = "http://api.ifood.tv/1.0/authenticate?";
	public static final String THIRD_PARTY_URL = "http://api.ifood.tv/1.0/3rd_party_login?";
	public static final String THIRD_URL = "http://api.ifood.tv/1.0/3rd_party_login?";
	public static final String UPLOAD_URL = "http://api.ifood.tv/2.0/pic/upload?auth-token=1212551";
	public static final String PIC_DETAILS = "http://api.ifood.tv/2.0/pic/doc_info?auth-token=1212551&id=";
	public static final String EDIT_DETAILS = "http://api.ifood.tv/2.0/pic/doc_edit?auth-token=1212551&id=";
	public static final String DELETE_URL = "http://api.ifood.tv/2.0/pic/doc_delete?auth-token=1212551&id=";
	public static final String PIC_LIST = "http://api.ifood.tv/2.0/pic/list?auth-token=1212551&sid=";
	public static final String ABOUT_URL = "http://api.ifood.tv/1.0/get_page_info?id=149";
	public static final String TERM_URL = "http://api.ifood.tv/1.0/get_page_info?id=152";
	public static final String AppLogger = "http://www.ifood.tv/applogger?appid=";
	
	public static final String LOG_TAG = "TwitterCon";
	public static final String TWITTER_ACCESS_TOKEN = "twitter_access_token";
	public static final String TWITTER_ACCESS_TOKEN_SECRET = "twitter_access_token_secret";
	public static final int TWITTER_LOGIN_CODE = 10;
	
	public static final String FACEBOOK_KEY = "122436831102289";
	public static final String TWITTER_CONSUMER_KEY = "Uve2bOpSJx97Y36ftsYKkw";
	public static final String TWITTER_CONSUMER_SECRET = "j4NeiisKHSU2Wgg1B1nEFEMO5XkahN8E8fBLHEeP0";
	
	public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
}
