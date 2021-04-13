package com.jotagalilea.posts.common.errorhandling

enum class AppAction constructor(val code: Long) {
	UNKNOWN(-1L),
	NONE(0L),
	GET_CREDENTIALS(1L),
	SIGN_IN(2L),
	SIGN_OUT(3L),
	GET_POSTS(4L),
	GET_COMMENTS(5L),
	GET_USER(6L);

	override fun toString(): String {
		return java.lang.Long.toString(this.code)
	}

	companion object {

		fun fromCode(code: Long): AppAction {
			var result = UNKNOWN

			val retryActions = values()
			var i = 0
			while (i < retryActions.size && result == UNKNOWN) {
				val retryAction = retryActions[i]
				if (retryAction.code == code) {
					result = retryAction
				}
				++i
			}

			return result
		}
	}
}