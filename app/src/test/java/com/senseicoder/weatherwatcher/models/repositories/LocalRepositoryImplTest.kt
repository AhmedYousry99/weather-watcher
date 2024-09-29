package com.senseicoder.weatherwatcher.models.repositories

import com.senseicoder.weatherwatcher.db.FakeLocalDataSource
import com.senseicoder.weatherwatcher.db.LocalDataSource
import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class LocalRepositoryImplTest {

    private lateinit var localRepository: LocalRepository
    private lateinit var fakePopulatedLocalDataSource: LocalDataSource


    private val alert1 = AlertDTO(1, "10:00 AM", "12:00PM", "12, Feb 2024", "12, Feb 2024", false)
    private val alert2 = AlertDTO(2, "11:00 AM", "1:00PM", "12, Feb 2024", "12, Feb 2024", true)
    private val alert3 = AlertDTO(3, "10:00 PM", "12:00AM", "12, Feb 2024", "13, Feb 2024", false)

    private val localTasks = mutableListOf(alert1, alert2)

    @Before
    fun setup(){
        fakePopulatedLocalDataSource = FakeLocalDataSource(
            mutableListOf(alert1, alert2, alert3)
        )
        localRepository = LocalRepositoryImpl.getInstance(
            fakePopulatedLocalDataSource
        )
    }

//    fun testGetAlerts() = runTest {
//        // Arrange
//        val expectedAlerts = listOf(/* your expected AlertDTO instances */)
//
//        // Act
//        val result = localRepository.getAlerts()
//
//        // Collect the results
//        val collectedAlerts = result.toList()
//
//        // Assert
//        assertThat(collectedAlerts, IsEqual(expectedAlerts))
//    }

    @Test
    fun getAlerts_ReturnAllAlerts() = runTest{
        //Arrange

        //Act
        val result: Flow<List<AlertDTO>> = localRepository.getAlerts()
        //Assert
        result.collect{
            assertThat(it, IsEqual(localTasks))
        }
    }

    @Test
    fun insertAlert_InsertItemNotInDB_ReturnSuccessWithValuePositive() = runTest{
//        //Arrange
//
//        //Act
//        localRepository.insertAlert(alert3)
//        //Assert
//        localRepository.getAlerts().collect(
//
//        )
//        var state: CurrentState<List<FavoriteDTO>> = favoritesViewModel.favorites.value
//        assertTrue(state is CurrentState.Success)
    }

    @Test
    fun insertAlert_ItemInDB_ReplaceAndReturnSuccessWithValuePositive() = runTest{
        //Arrange

        //Act
        favoritesViewModel.insertFavorite(alexandria)
        favoritesViewModel.insertFavorite(alexandria2)
        favoritesViewModel.getFavorites()
        //Assert
        var state: CurrentState<List<FavoriteDTO>> = favoritesViewModel.favorites.value
        assertTrue(state is CurrentState.Success)
    }

    @Test
    fun deleteAlert_ItemInDB_ReturnSuccessWithValuePositive() = runTest{
        //Arrange

        //Act
        favoritesViewModel.deleteFavorite(alexandria.location)
        favoritesViewModel.getFavorites()
        //Assert
        var state: CurrentState<List<FavoriteDTO>> = favoritesViewModel.favorites.value
        assertTrue(state is CurrentState.Success)
    }

    @Test
    fun deleteAlert_ItemNotInDB_ReturnSuccessWithValuePositive() = runTest{
        //Arrange

        //Act
        fakeRepository.insertFavorites(alexandria)
        favoritesViewModel.deleteFavorite(paris.location)
        //Assert
        var state: CurrentState<List<FavoriteDTO>> = favoritesViewModel.favorites.value
        assertTrue(state is CurrentState.Success)
    }

}