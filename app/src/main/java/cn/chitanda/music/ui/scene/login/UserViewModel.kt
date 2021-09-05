package cn.chitanda.music.ui.scene.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.chitanda.music.http.StateLiveData
import cn.chitanda.music.http.bean.LoginJson
import cn.chitanda.music.preference.CookiesPreference
import cn.chitanda.music.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *@author: Chen
 *@createTime: 2021/8/31 16:06
 *@description:
 **/
private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    cookiesPreference: CookiesPreference
) : ViewModel() {
    var cookies by cookiesPreference

    val user = StateLiveData<LoginJson>()

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.loginWithPassword(username, password, user)
        }
    }

    fun saveCookie(cookies: String) {
//        this.cookies = cookies
    }

    fun fetchUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.fetchUserInfo(user)
        }
    }
}