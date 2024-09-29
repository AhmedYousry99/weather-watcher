package com.senseicoder.weatherwatcher.features.drawer.favorites.viewmodels

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import com.senseicoder.weatherwatcher.models.repositories.FakeLocalRepo
import com.senseicoder.weatherwatcher.models.repositories.LocalRepository
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {

    private lateinit var favoritesViewModel: FavoriteViewModel
    private lateinit var fakeRepository: LocalRepository

    private val alexandria = FavoriteDTO("Alexandria", "29.97831", "31.871523")
    private val alexandria2 = FavoriteDTO("Alexandria", "30.97831", "40.871523")
    private val cairo = FavoriteDTO("Cairo", "23.97831", "31.871523")
    private val london = FavoriteDTO("London", "27.97831", "50.871523")
    private val paris = FavoriteDTO("Paris", "50.97831", "40.871523")

    @Before
    fun setup(){
        fakeRepository = FakeLocalRepo()
        favoritesViewModel = FavoriteViewModel(fakeRepository, ApplicationProvider.getApplicationContext() as Application)
    }

    @Test
    fun getFavorites_ReturnSuccessWithAllFavorites() = runTest{
        //Arrange
        //Act
        favoritesViewModel.getFavorites()
        // Collect the results
        val states = favoritesViewModel.favorites.value

        //Assert
        assertTrue(states is CurrentState.Success)
    }

    @Test
    fun insertFavorite_InsertItemNotInDB_ReturnSuccessWithValuePositive() = runTest{
    //Arrange

        //Act
        favoritesViewModel.insertFavorite(alexandria)
        favoritesViewModel.getFavorites()
        //Assert
        var state: CurrentState<List<FavoriteDTO>> = favoritesViewModel.favorites.value
        assertTrue(state is CurrentState.Success)
    }

    @Test
    fun insertFavorite_ItemInDB_ReplaceAndReturnSuccessWithValuePositive() = runTest{
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
    fun deleteFavorite_ItemInDB_ReturnSuccessWithValuePositive() = runTest{
        //Arrange

        //Act
        favoritesViewModel.deleteFavorite(alexandria.location)
        favoritesViewModel.getFavorites()
        //Assert
        var state: CurrentState<List<FavoriteDTO>> = favoritesViewModel.favorites.value
        assertTrue(state is CurrentState.Success)
    }

    @Test
    fun deleteFavorite_ItemNotInDB_ReturnSuccessWithValuePositive() = runTest{
        //Arrange

        //Act
        fakeRepository.insertFavorites(alexandria)
        favoritesViewModel.deleteFavorite(paris.location)
        //Assert
        var state: CurrentState<List<FavoriteDTO>> = favoritesViewModel.favorites.value
        assertTrue(state is CurrentState.Success)
    }
}