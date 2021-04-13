package com.jotagalilea.posts.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.jotagalilea.posts.model.*
import com.jotagalilea.posts.model.asDBObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


//@MediumTest
/**
 * Tests para algunas operaciones de la base de datos. Se utliza @SmallTest para realizar las
 * pruebas en memoria en lugar de en la base de datos real (en el almacenamiento del dispositivo).
 * Esto además agiliza las pruebas.
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class PostsDBTest{

	private var count = 0
	private lateinit var db: PostsDB
	private lateinit var dao: PostsDao
	private val post = com.jotagalilea.posts.model.Post(1, 1, "title", "body")
	private val comments = mutableListOf<com.jotagalilea.posts.model.Comment>().apply{
		while (count < 6)
			this.add(com.jotagalilea.posts.model.Comment(++count, 1, "name", "email", "body"))
	}
	private val user = com.jotagalilea.posts.model.User(
		1, "John Smith", "John", "js@sth.com",
		com.jotagalilea.posts.model.Address(
			"street", "suite", "city", "zipcode",
			com.jotagalilea.posts.model.Geolocation(1f, 1f),
		),
		"phone", "website",
		com.jotagalilea.posts.model.Company("name", "cp", "bs")
	)


	@get:Rule
	var instantTaskExecutorRule = InstantTaskExecutorRule()


	/**
	 * Se necesita inicializar la base de datos en memoria, pero no se accede a la base de datos real.
	 */
	@Before
	fun setup(){
		db = Room.inMemoryDatabaseBuilder(
			ApplicationProvider.getApplicationContext(),
			PostsDB::class.java
		).allowMainThreadQueries().build()
		dao = db.getDao()
	}


	/**
	 * Tras cada ejecución se cierra la base de datos.
	 */
	@After
	fun onFinish(){
		db.close()
	}


	/**
	 * Prueba de la inserción de un post y un usuario:
	 * - Se debe insertar antes el usuario que el post.
	 * - El post debe contener el id del usuario.
	 */
	@ExperimentalCoroutinesApi
	@Test
	fun insertPostAndUser() = runBlockingTest {
		dao.insertUser(user.asDBObject())
		dao.insertPost(post.asDBObject())
		val userFromDB = dao.getUserWithID(user.id).asDomainModel()
		val allPosts = dao.getAllPosts().asDomainModelMap().values.toList()
		assertThat(userFromDB).isEqualTo(user)
		assertThat(allPosts).contains(post)
		assertThat(post.userId).isEqualTo(userFromDB.id)
	}


	/**
	 * Prueba de la inserción de una serie de comentarios de un post:
	 * - Se debe insertar en orden el usuario, el post y finalmente los comentarios.
	 * - El post debe contener el id del usuario.
	 * - Los comentarios deben contener el id del post.
	 */
	@ExperimentalCoroutinesApi
	@Test
	fun insertCommentsOfPost() = runBlockingTest {
		dao.insertUser(user.asDBObject())
		dao.insertPost(post.asDBObject())
		dao.insertComments(comments.asDBObject())
		val posts = dao.getAllPosts().asDomainModelMap().values.toList()
		val commentsFromDB = dao.getCommentsFromPost(post.id).asDomainModel()
		val userFromDB = dao.getUserWithID(post.id)

		assertThat(post.userId).isEqualTo(userFromDB.id)
		assertThat(posts).contains(post)
		val it = commentsFromDB.iterator()
		while (it.hasNext())
			assertThat(it.next().postId).isEqualTo(post.id)
	}

}