package com.dangerfield.spyfall.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.api.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class DBCleaner(private val db : FirebaseFirestore, private val constants: Constants) {

    fun cleandb() : LiveData<Resource<String, String>> {
        val result =  MutableLiveData<Resource<String, String>>()
        val now = System.currentTimeMillis() / 1000
        db.collection(constants.games_prod).whereLessThanOrEqualTo(Constants.GameFields.expiration, now).get().addOnSuccessListener {
            CoroutineScope(IO).launch {
                it.documents.toList().pmap {game -> db.collection(constants.games_prod).document(game.id).delete() }
                val message = "Deleted ${it.documents.size} expired games"
                result.postValue(Resource.Success(message))
            }
        }.addOnFailureListener {
            result.postValue(Resource.Error(error = "Failed to delete expired games"))
        }
        return result
    }
}