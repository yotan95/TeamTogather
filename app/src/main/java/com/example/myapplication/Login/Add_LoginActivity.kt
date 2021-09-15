package com.example.myapplication.Login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.Main.Activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_login.*
import kotlinx.android.synthetic.main.view_item_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import android.R

import android.graphics.Bitmap
import android.widget.ImageView
import java.io.InputStream


class Add_LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var PICK_IMAGE_FROM_ALBUM = 1
    private var photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
    private var storage: FirebaseStorage? = null
    private var firestore: FirebaseFirestore? = null
    private var gender: String? = null
    private var photoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.myapplication.R.layout.activity_add_login)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        //fireStorage 초기화
        storage = FirebaseStorage.getInstance()
        //fireStore Database
        firestore = FirebaseFirestore.getInstance()
        //firebase Auth
        auth = FirebaseAuth.getInstance()

        //갤러리 오픈
        // ACTION_GET_CONTENT : YOU CAN CHOOSE SOMETHING BASED ON MIME type
        // or ACTION_PICK

        //라디오 버튼 값 설정
       add_login_sex_input_set.setOnCheckedChangeListener { _, checked ->
            when (checked) {
                com.example.myapplication.R.id.add_login_male_btn -> gender = "남"
                com.example.myapplication.R.id.add_login_female_btn -> gender = "여"
            }
        }
        //이미지 업로드 이벤트 처리
        upload.setOnClickListener {

            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
        upload_sign.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                contentUpload()
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        photoUri = Uri.parse("android.resource://com.example.myapplication/ic_baseline_account_circle_24")
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            photoUri = data?.data!!
            upload_image.setImageURI(photoUri)
        } else {
            finish()
        }
    }

    //좋은 코드는 아니지만 간소화 하는 방법을... 모색
    fun contentUpload(){
        //ProgressBar <<< 효과 넣을지 말지? 살짝 구시대적 ui
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_"+timeStamp+"_.png"
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val NM = add_login_nickname_edit.text.toString()
        val profile_timestamp = System.currentTimeMillis()
        val name = add_login_edit.text.toString()

        if (photoUri != null) {
            photoUri =
                Uri.parse("android.resource://com.example.myapplication/drawable/ic_baseline_account_circle_signiture")
            // images/(imageFilename) 위치를 가리키는 참조 변수-> 를 putFile로 storage서버에 업로드
            val storageRef = storage?.reference?.child("Profiles")?.child(imageFileName)
            // storageRef?.putFile()의 반환값은 StorageTask
            storageRef?.putFile(photoUri!!)?.addOnSuccessListener { taskSnapshot ->
                //firebase Strage 서버에 저장된 파일 다운로드 URL 가져옴
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    var UR = uri.toString()
                    firestore?.collection("userid")?.document(uid)?.update(
                        mapOf(
                            "profileUrl" to UR,
                            "nickname" to NM,
                            "profile_timestamp" to profile_timestamp,
                            "name" to name,
                            "gender" to gender
                        )
                    )
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }else{
            firestore?.collection("userid")?.document(uid)?.update(
                mapOf(
                    "nickname" to NM,
                    "profile_timestamp" to profile_timestamp,
                    "profileUrl" to "null",
                    "name" to name,
                    "gender" to gender
                )
            )
            setResult(RESULT_OK)
            finish()
        }

    }
}