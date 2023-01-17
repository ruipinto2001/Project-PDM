package pt.ipca.sermo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream


class ProfileActivity : AppCompatActivity()
{
    private val code = 1001

    // Get fields from XML
    private val usernameTV: TextView by lazy {
        findViewById<TextView>(R.id.profile_username_textview) }
    private val emailTV: TextView by lazy {
        findViewById<TextView>(R.id.profile_email_textview) }
    private val birthdayTV: TextView by lazy {
        findViewById<TextView>(R.id.profile_birthday_textview) }
    private val genderTV: TextView by lazy {
        findViewById<TextView>(R.id.profile_gender_textview) }
    private val pictureIB: ImageButton by lazy {
        findViewById<ImageButton>(R.id.profile_photo_button) }

    private val buttonClick = AlphaAnimation(1f, 0.8f)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        loadUserInfo()
    }

    fun closeProfile(view: View)
    {
        view.startAnimation(buttonClick);
        finish()
    }

    private fun loadUserInfo()
    {
        // Get the uid of the current user
        val userId = Firebase.auth.currentUser!!.uid

        val db = Firebase.firestore
        val docRef = db.collection("Users").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                // If the user was found
                if (document != null && document.exists())
                {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")

                    val username = document.getString("username")
                    usernameTV.text = username

                    val email = document.getString("email")
                    emailTV.text = email

                    val birthday = document.getString("birthday")
                    birthdayTV.text = birthday

                    val gender = document.getString("gender")
                    genderTV.text = gender

                    val photoEncoded: String? = document.getString("profilePicture")
                    if (photoEncoded != null && photoEncoded != "EMPTY")
                    {
                        val photoBitmap = convertBase64toBitmap(photoEncoded)
                        pictureIB.setImageBitmap(photoBitmap)
                    }
                }
                else
                {
                    Log.d(TAG, "No such document")
                    Toast.makeText(this,"User not found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    companion object { private const val TAG = "Profile" }

    fun editPhoto(view: View) {
        val intent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == code)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                val resultUri: Uri? = data?.data
                if (resultUri == null)
                    Toast.makeText(this, "Error adding photo!", Toast.LENGTH_LONG).show()
                else
                {
                    val encodedImage = convertImageBase64(resultUri)
                    uploadImage(encodedImage)
                }
            }
        }
    }

    private fun convertImageBase64(imageUri: Uri): String
    {
        // Convert URI to Bitmap
        var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

        // Compress bitmap
        bitmap = compressBitmap(bitmap, 1)

        // Convert Bitmap to Base64
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val encodedString: String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        return encodedString
    }

    private fun convertBase64toBitmap(encodedImage: String): Bitmap
    {
        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        return decodedByte
    }

    private fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP,quality,stream)
        val byteArray = stream.toByteArray()

        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }

    private fun uploadImage(encodedImage: String)
    {
        val userId = Firebase.auth.currentUser!!.uid
        val db = Firebase.firestore
        val docRefUpdate = db.collection("Users").document(userId)
        docRefUpdate.update("profilePicture", encodedImage).addOnSuccessListener {
            loadUserInfo()
            Log.d(TAG, "Image uploaded successfully!")
        }.addOnFailureListener{
                e -> Log.w(TAG, "Error updating image", e)
        }
    }
}