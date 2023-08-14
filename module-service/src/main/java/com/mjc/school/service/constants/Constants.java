package com.mjc.school.service.constants;

public final class Constants {

	public static final long ID_MIN_VALUE = 1L;
	public static final long ID_VALUE_MAX = Long.MAX_VALUE;
	public static final int AUTHOR_NAME_LENGTH_MIN = 3;
	public static final int AUTHOR_NAME_LENGTH_MAX = 15;
	public static final String AUTHOR_ENTITY_NAME = "author";
	public static final String COMMENT_ENTITY_NAME = "comment";
	public static final String NEWS_ENTITY_NAME = "news";
	public static final String TAG_ENTITY_NAME = "tag";
	public static final int COMMENT_CONTENT_LENGTH_MIN = 5;
	public static final int COMMENT_CONTENT_LENGTH_MAX = 255;
	public static final int NEWS_TITLE_LENGTH_MIN = 5;
	public static final int NEWS_TITLE_LENGTH_MAX = 30;
	public static final int NEWS_CONTENT_LENGTH_MIN = 5;
	public static final int NEWS_CONTENT_LENGTH_MAX = 255;
	public static final int TAG_NAME_LENGTH_MIN = 3;
	public static final int TAG_NAME_LENGTH_MAX = 15;

	private Constants() {
		// Empty. Hides default public constructor
	}
}