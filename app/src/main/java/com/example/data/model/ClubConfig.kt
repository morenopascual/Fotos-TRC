package com.example.data.model

data class ClubConfig(
    val clubName: String = "Torrelodones Rugby Club",
    val googlePhotosAlbumUrl: String = "https://photos.app.goo.gl/trc-torrelodones-rugby",
    val communicationEmail: String = "comunicacion@torrelodonesrugby.com",
    val defaultCategory: RugbyCategory = RugbyCategory.SUB_14,
    val stadiumName: String = "Campo Julián Ariza",
    val webhookUploadUrl: String = "",
    val autoShareToGooglePhotos: Boolean = true
)
