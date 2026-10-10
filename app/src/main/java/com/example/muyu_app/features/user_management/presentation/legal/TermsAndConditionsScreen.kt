package com.example.muyu_app.features.user_management.presentation.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray

@Composable
fun TermsAndConditionsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(MuyuPageGray)) {
        Row(
            Modifier.fillMaxWidth().background(MuyuOlive).statusBarsPadding().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("‹", color = Color.White, fontSize = 40.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(16.dp))
            Text("Términos y condiciones", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Última actualización: 10 de octubre de 2026", color = Color.Gray, fontSize = 13.sp)
            LegalSection("1. Aceptación del servicio", "Al crear una cuenta y utilizar Muyu, el usuario declara haber leído y aceptado estos términos. Muyu facilita la coordinación entre agricultores y comerciantes, el registro de parcelas, evidencias de campo, hitos, alertas y acuerdos de financiamiento.")
            LegalSection("2. Uso responsable de la cuenta", "El usuario debe proporcionar información verdadera, proteger sus credenciales y utilizar la plataforma de forma lícita. No está permitido suplantar identidades, manipular evidencias, registrar información falsa ni utilizar Muyu para actividades fraudulentas.")
            LegalSection("3. Evidencias y ubicación", "Las fotografías, fecha, hora, ubicación GPS, notas e información técnica registradas como evidencia deben corresponder al trabajo realizado. El usuario autoriza su almacenamiento y visualización por las partes vinculadas al acuerdo para validar el cumplimiento de los hitos.")
            LegalSection("4. Parcelas, acuerdos y pagos", "La publicación de una parcela no garantiza su financiamiento. Los fondos permanecen en custodia y solo se liberan según las condiciones del acuerdo y la aprobación de los hitos. Muyu no reemplaza contratos legales, asesoría financiera ni inspecciones profesionales.")
            LegalSection("5. Privacidad y datos personales", "Los datos personales se utilizan para identificar al usuario, operar la cuenta, mostrar parcelas y facilitar los acuerdos. La cámara y la ubicación se solicitan únicamente cuando una función las necesita. Muyu debe aplicar medidas razonables de seguridad y no comercializar datos personales sin autorización.")
            LegalSection("6. Propiedad y contenido", "El usuario conserva la titularidad del contenido que registra y concede a Muyu permiso para procesarlo dentro de las funciones del servicio. El usuario es responsable de contar con autorización para publicar fotografías, documentos y demás información.")
            LegalSection("7. Disponibilidad y limitaciones", "La disponibilidad puede verse afectada por conectividad, mantenimiento o servicios externos. En zonas sin conexión, ciertos registros podrán mantenerse pendientes hasta su sincronización. La versión académica de Muyu es un prototipo y no debe utilizarse para operaciones financieras reales.")
            LegalSection("8. Ética y responsabilidad profesional", "El desarrollo de Muyu se guía por honestidad, transparencia, privacidad, calidad y respeto hacia los usuarios, de acuerdo con los principios éticos de ACM/IEEE y del Colegio de Ingenieros del Perú. El equipo mantiene trazabilidad de los cambios mediante control de versiones y documentación del proceso de ingeniería de software.")
            LegalSection("9. Cambios y terminación", "Muyu podrá actualizar estos términos cuando el producto evolucione. El usuario podrá dejar de utilizar el servicio y solicitar el cierre de su cuenta. El acceso podrá suspenderse cuando se detecte fraude, abuso o incumplimiento de estas condiciones.")
            LegalSection("10. Contacto", "Las consultas, solicitudes de privacidad o reportes de incidencias podrán dirigirse al equipo de AllpaTech mediante los canales de soporte indicados en la aplicación.")
            Box(
                Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp).background(MuyuOlive, RoundedCornerShape(18.dp)).clickable { onBack() }.padding(18.dp),
                contentAlignment = Alignment.Center,
            ) { Text("Entendido", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        }
    }
}

@Composable
private fun LegalSection(title: String, body: String) {
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF245B27))
        Spacer(Modifier.height(8.dp))
        Text(body, fontSize = 15.sp, lineHeight = 21.sp, color = Color(0xFF333333))
    }
}
