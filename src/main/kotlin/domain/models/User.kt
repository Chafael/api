package com.sylvara.domain.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class User(
    // 1. IMPORTANTE: Ponemos '= 0' para que al recibir el JSON de creación
    // no nos exija este campo (ya que la BD lo genera después).
    val userId: Int = 0,

    val userName: String,
    val userLastname: String,

    // 2. Conectamos el serializador de LocalDate
    @Serializable(with = LocalDateSerializer::class)
    val userBirthday: LocalDate,

    val userEmail: String,
    val userPassword: String,
    val biography: String? = null,


    // 3. Conectamos el serializador de LocalDateTime y lo hacemos nullable
    // para que use el default de la base de datos si no viene en el JSON.
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null
) {
    // Tus validaciones están PERFECTAS. Mantenlas así.
    init {
        require(userName.isNotBlank()) { "El nombre no puede estar vacío" }
        require(userLastname.isNotBlank()) { "El apellido no puede estar vacío" }
        require(userEmail.contains("@")) { "Email inválido" }
        require(userPassword.length >= 8) { "La contraseña debe tener al menos 8 caracteres" }
    }
}

// --- Serializadores Personalizados (Copia esto al final del archivo) ---

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), DateTimeFormatter.ISO_LOCAL_DATE)
    }
}

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        // Si tienes problemas con milisegundos, usa ISO_LOCAL_DATE_TIME
        encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}