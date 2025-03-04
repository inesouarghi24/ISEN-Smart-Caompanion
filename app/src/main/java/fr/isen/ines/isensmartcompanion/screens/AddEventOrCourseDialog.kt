import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddEventOrCourseDialog(
    isCourse: Boolean,
    onAddEvent: (String, String) -> Unit,
    onAddCourse: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Ajouter un ${if (isCourse) "Cours" else "Événement"}") },
        text = {
            Column {
                if (isCourse) {
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Matière") })
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Heure") })
                    OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Salle") })
                } else {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre") })
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lieu") })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isCourse) {
                    onAddCourse(time, room, subject)
                } else {
                    onAddEvent(title, location)
                }
                onDismiss()
            }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Annuler")
            }
        }
    )
}
