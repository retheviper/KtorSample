package com.retheviper.common.extension

import com.toxicbakery.bcrypt.Bcrypt

/**
 * Hash string in UTF-8 encoding.
 */
fun String.hash(): String =
    Bcrypt.hash(this, 5).toString(Charsets.UTF_8)

/**
 * Verify string with another.
 */
fun String.verifyWith(encoded: String): Boolean =
    Bcrypt.verify(this, encoded.encodeToByteArray())