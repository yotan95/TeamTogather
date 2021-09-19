package com.example.myapplication.Main.Activity


//import com.example.myapplication.Main.Fragment.ChatFragment
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.Main.Fragment.*
import com.example.myapplication.Main.Fragment.BoardFragment.BoardFragment
import com.example.myapplication.Main.Fragment.BoardFragment.repo.Repo
import com.example.myapplication.Main.Fragment.HomeFragment.HomeFragment
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
//import com.gun0912.tedpermission.PermissionListener
//import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frag_map.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.ArrayList


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(){
    // MainActivity가 가지고 있는 멤버 변수 선언
    private lateinit var homeFragment: HomeFragment
    private lateinit var mapFragment: CurrentPlaceFragment
    private lateinit var boardFragment : BoardFragment
    //private lateinit var chatFragment: ChatFragment
    private lateinit var settingFragment: SettingFragment
    private var repo = Repo.StaticFunction.getInstance()

    init {
        // board List initial For boardFragment
        repo.getboarddata()
        repo.getboardUid()
    }

    companion object {
        const val TAG: String = "로그"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavi.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)
        //권한 설
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//            1
//        )
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.frame_container, homeFragment).commit()


    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause: ", )
    }   

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart: ", )   
    }

    //바텀 네비게이션 아이템 클릭 리스너

    private val onBottomNavItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.action_home -> {
                    homeFragment = HomeFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, homeFragment).commit()
                }
                R.id.action_Map -> {
                    mapFragment = CurrentPlaceFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, mapFragment).commit()
                }
                R.id.action_board -> {
                    // 사진을 가져올 수 있는지 확인 하는 작업
                    boardFragment = BoardFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, boardFragment).commit()
                }
               /* R.id.action_chat -> {
//                    chatFragment = ChatFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, chatFragment).commit()
                }*/
                R.id.action_setting -> {
                    settingFragment = SettingFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, settingFragment).commit()
                }
            }
            true
        }
}