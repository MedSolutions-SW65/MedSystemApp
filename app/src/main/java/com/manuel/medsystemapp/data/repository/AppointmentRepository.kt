package com.manuel.medsystemapp.data.repository

import com.manuel.medsystemapp.common.Resource
import com.manuel.medsystemapp.data.remote.AppointmentService
import com.manuel.medsystemapp.data.remote.AppointmentDto
import com.manuel.medsystemapp.data.remote.toAppointment
import com.manuel.medsystemapp.domain.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AppointmentRepository(private val service: AppointmentService) {

    suspend fun getAppointments(): Resource<List<Appointment>> = withContext(Dispatchers.IO) {
        try {
            val response = service.getAppointments()
            if (response.isSuccessful) {
                response.body()?.let { appointmentsDto: List<AppointmentDto> ->
                    val appointments = appointmentsDto.map { appointmentDto: AppointmentDto ->
                        appointmentDto.toAppointment()
                    }.toList()
                    return@withContext Resource.Success(data = appointments)
                }
            }
            return@withContext Resource.Error(message = response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(message = e.message ?: "An exception occurred.")
        }
    }

    suspend fun postAppointments(appointment: Appointment): Resource<Appointment> = withContext(Dispatchers.IO){
        try {
            //val response = service.createAppointment(appointment.toAppointmentDto())
            val appointmentDto = AppointmentDto(
                appointment.id,
                appointment.doctorId,
                appointment.patientId,
                appointment.date,
                appointment.reason
            )
            val response = service.postAppointment(appointmentDto)
            if (response.isSuccessful) {
                response.body()?.let { postAppointmentDto: AppointmentDto ->
                    return@withContext Resource.Success(data = appointmentDto.toAppointment())
                }
            }
            return@withContext Resource.Error(message = response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(message = e.message ?: "An exception occurred.")
        }
    }
}