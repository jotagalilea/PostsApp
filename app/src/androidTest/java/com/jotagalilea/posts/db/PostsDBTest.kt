package com.jotagalilea.posts.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.jotagalilea.posts.model.*
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

	private lateinit var db: PostsDB
	private lateinit var dao: PostsDao

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
		val post = Post(1,1,"title","body")
		val user = User(1,"John Smith","John","js@sth.com",
			Address(
				"street","suite", "city","zipcode",
				Geolocation(1f, 1f),
			),
			"phone", "website",
			Company("name", "cp", "bs")
		)
		dao.insertUser(user.asDBObject())
		dao.insertPost(post.asDBObject())
		val userFromDB = dao.getUserWithID(user.id)?.asDomainModel()
		val allPosts = dao.getAllPosts().asDomainModelMap().values.toList()
		assertThat(userFromDB).isEqualTo(user)
		assertThat(allPosts).contains(post)
		assertThat(post.userId).isEqualTo(userFromDB?.id)
	}

}