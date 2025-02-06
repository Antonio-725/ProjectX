import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.User
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt // Import the Bcrypt library

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    fun registerUser(
        username: String,
        password: String,
        role: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                onError("Username already exists")
                return@launch
            }

            // Hash the password securely
            val hashedPassword = hashPassword(password)
            val user = User(username = username, password = hashedPassword, role = role)

            try {
                userDao.insert(user)
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to register user: ${e.message}")
            }
        }
    }

    private fun hashPassword(password: String): String {
        // Use Bcrypt to hash the password with a salt
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

}
