package com.example.neurozen_front.neurozen.data.network

import com.google.gson.annotations.SerializedName

// --- Auth Models ---
data class SignInRequest(
    val username: String,
    val password: String
)

data class SignInResponse(
    val id: String,
    val username: String,
    val token: String
)

data class AuthSession(
    val token: String,
    val userId: String,
    val username: String,
    val email: String
)

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String
)

data class SignUpResponse(
    val id: String,
    val username: String,
    val email: String
)

// --- Professionals Models ---
data class ProfessionalResource(
    @SerializedName(value = "id", alternate = ["Id"]) val id: String,
    @SerializedName(value = "firstName", alternate = ["Name", "name", "FirstName"]) val firstName: String,
    @SerializedName(value = "lastName", alternate = ["LastName", "lastname"]) val lastName: String? = "",
    @SerializedName(value = "specialization", alternate = ["Specialty", "specialty", "Specialization"]) val specialization: String,
    @SerializedName(value = "email", alternate = ["Email"]) val email: String? = "",
    @SerializedName(value = "phone", alternate = ["Phone"]) val phone: String? = "",
    @SerializedName(value = "imageUrl", alternate = ["Image", "image", "ImageUrl"]) val imageUrl: String,
    @SerializedName(value = "rating", alternate = ["Rating"]) val rating: Double? = 0.0,
    @SerializedName(value = "experience", alternate = ["Experience"]) val experience: String? = "",
    @SerializedName(value = "bio", alternate = ["Bio"]) val bio: String? = "",
    @SerializedName(value = "price", alternate = ["Price"]) val price: Double? = 0.0,
    @SerializedName(value = "availability", alternate = ["Availability"]) val availability: String? = ""
)

// --- Appointments Models ---
data class AppointmentRequest(
    @SerializedName(value = "patientId", alternate = ["PatientId"]) val patientId: String,
    @SerializedName(value = "professionalId", alternate = ["ProfessionalId"]) val professionalId: String,
    @SerializedName(value = "scheduledAt", alternate = ["ScheduledAt"]) val scheduledAt: String,
    @SerializedName(value = "appointmentType", alternate = ["AppointmentType"]) val appointmentType: Int
)

data class AppointmentResponse(
    @SerializedName(value = "id", alternate = ["Id"]) val id: String,
    @SerializedName(value = "patientId", alternate = ["PatientId"]) val patientId: String,
    @SerializedName(value = "professionalId", alternate = ["ProfessionalId"]) val professionalId: String,
    @SerializedName(value = "appointmentDate", alternate = ["AppointmentDate"]) val appointmentDate: String,
    @SerializedName(value = "status", alternate = ["Status"]) val status: String,
    @SerializedName(value = "professionalName", alternate = ["ProfessionalName"]) val professionalName: String? = null
)

// --- Dashboard / Home Compatibility Models ---
data class DashboardResponse(
    @SerializedName(value = "stressLevel", alternate = ["StressLevel"]) val stressLevel: Int,
    @SerializedName(value = "heartRate", alternate = ["HeartRate"]) val heartRate: Int,
    @SerializedName(value = "sleepHours", alternate = ["SleepHours"]) val sleepHours: Int,
    @SerializedName(value = "nextAppointment", alternate = ["NextAppointment"]) val nextAppointment: NextAppointment?
)

data class NextAppointment(
    @SerializedName(value = "id", alternate = ["Id"]) val id: String,
    @SerializedName(value = "appointmentDate", alternate = ["AppointmentDate"]) val appointmentDate: String,
    @SerializedName(value = "professionalName", alternate = ["ProfessionalName"]) val professionalName: String,
    @SerializedName(value = "professionalSpecialty", alternate = ["ProfessionalSpecialty", "Specialty"]) val professionalSpecialty: String,
    @SerializedName(value = "status", alternate = ["Status"]) val status: String
)

data class MeditationResource(
    @SerializedName(value = "id", alternate = ["Id"]) val id: String,
    @SerializedName(value = "title", alternate = ["Title"]) val title: String,
    @SerializedName(value = "description", alternate = ["Description"]) val description: String,
    @SerializedName(value = "durationMinutes", alternate = ["DurationMinutes"]) val durationMinutes: Int,
    @SerializedName(value = "imageUrl", alternate = ["ImageUrl", "Image"]) val imageUrl: String,
    @SerializedName(value = "audioUrl", alternate = ["AudioUrl", "Audio"]) val audioUrl: String
)

// --- Subscriptions Models ---
data class SubscriptionRequest(
    @SerializedName(value = "userId", alternate = ["UserId"]) val userId: String,
    @SerializedName(value = "planId", alternate = ["PlanId"]) val planId: Int,
    @SerializedName(value = "nameUser", alternate = ["NameUser"]) val nameUser: String,
    @SerializedName(value = "lastNameUser", alternate = ["LastNameUser"]) val lastNameUser: String,
    @SerializedName(value = "emailUser", alternate = ["EmailUser"]) val emailUser: String
)

data class SubscriptionResource(
    @SerializedName(value = "id", alternate = ["Id"]) val id: String,
    @SerializedName(value = "userId", alternate = ["UserId"]) val userId: String,
    @SerializedName(value = "planName", alternate = ["PlanName"]) val planName: String,
    @SerializedName(value = "status", alternate = ["Status"]) val status: String,
    @SerializedName(value = "startDate", alternate = ["StartDate"]) val startDate: String,
    @SerializedName(value = "endDate", alternate = ["EndDate"]) val endDate: String
)

// --- Health Metrics Models (Triggers/Emociones) ---
data class HealthMetricRequest(
    @SerializedName(value = "userId", alternate = ["UserId"]) val userId: String,
    @SerializedName(value = "stressLevel", alternate = ["StressLevel"]) val stressLevel: Int,
    @SerializedName(value = "heartRate", alternate = ["HeartRate"]) val heartRate: Int,
    @SerializedName(value = "sleepHours", alternate = ["SleepHours"]) val sleepHours: Int,
    @SerializedName(value = "notes", alternate = ["Notes"]) val notes: String
)

data class HealthMetricResource(
    @SerializedName(value = "id", alternate = ["Id"]) val id: String,
    @SerializedName(value = "userId", alternate = ["UserId"]) val userId: String,
    @SerializedName(value = "stressLevel", alternate = ["StressLevel"]) val stressLevel: Int,
    @SerializedName(value = "heartRate", alternate = ["HeartRate"]) val heartRate: Int,
    @SerializedName(value = "sleepHours", alternate = ["SleepHours"]) val sleepHours: Int,
    @SerializedName(value = "notes", alternate = ["Notes"]) val notes: String,
    @SerializedName(value = "createdAt", alternate = ["CreatedAt"]) val createdAt: String
)
