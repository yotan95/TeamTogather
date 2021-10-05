package com.example.myapplication.Main.Board

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.DTO.BoardDTO
import com.example.myapplication.KeyboardVisibilityUtils
import com.example.myapplication.Main.Fragment.BoardFragment.Recent.repo.Repo
import com.example.myapplication.R
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_login.*
import com.google.android.gms.tasks.*
import kotlinx.android.synthetic.main.activity_board_post.*
import kotlinx.android.synthetic.main.activity_board_post.sv_root
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BoardPost : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 1
    private lateinit var auth: FirebaseAuth
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    private var photoUri: Uri? = null
    private var uid: String? = null
    private var NM: String? = null
    private var gender: String? = null
    private var profile: String? = null
    private var longitude: Double? = null
    private var latitude: Double? = null
    private var locationName : String? = null////
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private var postViewModel = PostViewModel()
    private lateinit var postAdapter: PostAdapter

    private fun gettextList(): ArrayList<String> {
        return arrayListOf<String>("치킨","피자","중식","한식","양식","커피","돈까스","일식")
    }
    private var repo = Repo.StaticFunction.getInstance()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_post)


        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                sv_root.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })  //키보드 움직이기
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //fireStorage 초기화
        storage = FirebaseStorage.getInstance()
        //fireStore Database
        firestore = FirebaseFirestore.getInstance()
        //firebase Auth
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                latitude = location!!.latitude
                longitude = location!!.longitude
                locationName = getCityName1(latitude!!,longitude!!)////
            }.addOnFailureListener {
            }
        viewPager2.adapter = postAdapter
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        lifecycleScope.launch(Dispatchers.IO) {
            firestore?.collection("userid")?.document(uid!!)?.get()?.addOnSuccessListener {
                if (it != null) {
                    NM = it["nickname"].toString()
                    profile = it["profileUrl"].toString()
                    gender = it["gender"].toString()

                }
            }
        }
        upload_BoardImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
            hide_layout.visibility = View.VISIBLE
        }
        btn_write.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                boardUpload()
            }

        }
    }

    ////
    private fun getCityName1(lat: Double, long: Double): String {
        var cityName: String = ""
        var doName: String = ""

        val geoCoder = Geocoder(this, Locale.getDefault())
        val Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        doName = Adress.get(0).thoroughfare


        //Toast.makeText(context, cityName + " " + doName + " " + jibunName, Toast.LENGTH_LONG).show()
        return "$cityName, $doName"
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            photoUri = data?.data
            layout_imageUrlWrite.setImageURI(photoUri)
        } else {
            finish()
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun boardUpload() {
        val timeStamp = SimpleDateFormat("yyyy.MM.dd_HH:mm").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_Board.png"
        val storageRef = storage?.reference?.child("Board")?.child(imageFileName)
        // promise 방식
        val boardDTO = BoardDTO()
        boardDTO.contents = board_context.text.toString()
        boardDTO.postTitle = title_text.text.toString()
        boardDTO.Writed_date = timeStamp
        boardDTO.imageWriteExplain = imgaeExplain.text.toString()
        boardDTO.ProfileUrl = profile
        // System.currentTimeMillis() : 올린 시간을 mm/sec으로 변환
        boardDTO.timestamp = System.currentTimeMillis()
        boardDTO.nickname = NM
        boardDTO.uid = auth.currentUser?.uid
        boardDTO.longitude = longitude
        boardDTO.latitude = latitude
        boardDTO.gender = gender
        boardDTO.locationName = locationName////

        if (photoUri != null) {
            storageRef?.putFile(photoUri!!)
                ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }?.addOnSuccessListener { uri ->
                    boardDTO.imageUrlWrite = uri.toString()

                    FirebaseFirestore.getInstance().collection("Board").document()
                        .set(boardDTO)
                    setResult(RESULT_OK)

                }
        } else {
            FirebaseFirestore.getInstance().collection("Board").document()
                .set(boardDTO)
            setResult(RESULT_OK)
        }
//        val intent = Intent(this, Main::class.java)

        //startActivity(intent)
        finish()
    }
    override fun onPause() {
        super.onPause()
        repo.upDateOnlineState("offline")
    }

    override fun onResume() {
        super.onResume()
        repo.upDateOnlineState("online")
    }

    private fun initializeSelectedMonth() {
        if (postViewModel.selectedMonth.value == null) {
            val now = postViewModel. .months[LocalDate.now().monthValue]
            mainViewModel.setSelectedMonth(now)
            scrollToMonth(now)
        }
    }
    private fun scrollToMonth(month: LocalDate) {
        var width = month_list.width

        if (width > 0) {
            val monthWidth = month_item.width
            layoutManager.scrollToPositionWithOffset(mainViewModel.months.indexOf(month), width / 2 - monthWidth / 2)
        } else {
            val vto = month_list.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    month_list.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = month_list.width
                    context?.resources?.getDimensionPixelSize(R.dimen.month_item_width)
                        ?.let { monthWidth ->
                            layoutManager.scrollToPositionWithOffset(
                                mainViewModel.months.indexOf(
                                    month
                                ), width / 2 - monthWidth / 2
                            )
                        }
                }
            })
        }
    }
}