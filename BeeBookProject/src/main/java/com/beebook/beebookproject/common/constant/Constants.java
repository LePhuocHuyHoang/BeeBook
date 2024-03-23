package com.beebook.beebookproject.common.constant;

/**
 * This class is used to declare constant for project
 * 
 * @author Red Devil
 * @since 2020-10-10
 */
public class Constants {

	/** Common character */
	public static final String COMMON_UTF8 = "UTF-8";
	public static final String COMMON_SPACE = " ";
	public static final String COMMON_HYPHEN = "-";
	public static final String COMMON_SORT_ASC = "asc";
	public static final String COMMON_SORT_DESC = "desc";

	/** Date Format */
	public static final String DATE_FORMAT_FOR_FILE_NAME = "yyyyMMdd-HHmm";

	/** Common property key */
	public static final String PROP_KEY_ROOT_FOLDER = "root.storage.folder";

	/** The number of record per each page */
	public static final String PAGE_SIZE = "2";

	/** Response Code */
	public static final int RESULT_CD_FAIL = 0;
	public static final int RESULT_CD_DUPL = 1;
	public static final int RESULT_CD_SUCCESS = 100;

	public static final String ID = "id";
	public static final String PAGE_NUMBER = "0";
	public static final String SORT_CATEGORIES_BY = "categoryId";
	public static final String SORT_PRODUCTS_BY = "productId";
	public static final String SORT_USERS_BY = "userId";
	public static final String SORT_ORDERS_BY = "totalAmount";
	public static final String SORT_DIR = "asc";
	public static final Long ADMIN_ID = 101L;
	public static final Long USER_ID = 102L;
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	public static final String[] PUBLIC_URLS = { "/v3/api-docs/**", "/swagger-ui/**", "/api/register/**", "/api/login" };
	public static final String[] USER_URLS = { "/api/public/**" };
	public static final String[] ADMIN_URLS = { "/api/admin/**" };
}