package com.jotagalilea.posts.model.domainmodel

import java.io.Serializable

/**
 * Clase que contiene los datos de un usuario.
 */
data class User(
	var id: Int,
	var name: String,
	var userName: String,
	var email: String,
	var address: Address,
	var phone: String,
	var website: String,
	var company: Company
): Serializable