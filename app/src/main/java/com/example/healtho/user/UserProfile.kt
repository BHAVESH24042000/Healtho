package com.example.healtho.user


import java.io.Serializable
import java.util.*


class UserProfile(
    private val id: String?,
    val name: String?,
    val nickname: String?,
    val pictureURL: String?,
    val email: String?,
    val isEmailVerified: Boolean?,
    val familyName: String?,
    val createdAt: Date?,
    /**
     * List of the identities from a Identity Provider associated to the user.
     *
     * @return a list of identity provider information.
     */
    //private val identities: List<UserIdentity>?,
    private val extraInfo: Map<String, Any>?,
    private val userMetadata: Map<String, Any>?,
    private val appMetadata: Map<String, Any>?,
    public val givenName: String?
) : Serializable {

    /**
     * Getter for the unique Identifier of the user. If this represents a Full User Profile (Management API) the 'id' field will be returned.
     * If the value is not present, it will be considered a User Information and the id will be obtained from the 'sub' claim.
     *
     * @return the unique identifier of the user.
     */
    fun getId(): String? {
        if (id != null) {
            return id
        }
        return if (getExtraInfo().containsKey("sub")) getExtraInfo()["sub"] as String? else null
    }

    fun getUserMetadata(): Map<String, Any> {
        return userMetadata ?: emptyMap()
    }

    fun getAppMetadata(): Map<String, Any> {
        return appMetadata ?: emptyMap()
    }


    fun getExtraInfo(): Map<String, Any> {
        return extraInfo?.toMap() ?: emptyMap()
    }
}