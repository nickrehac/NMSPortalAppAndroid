package rehac.nick.portalcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme
import kotlin.math.min

class CoordinateConversionActivity : ComponentActivity() {
    private lateinit var viewModel: CoordinateConversionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CoordinateConversionViewModel::class.java]
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.slide_out_left)
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        setContent {
            MainContent()
        }
    }

    @Composable
    fun MainContent(modifier: Modifier = Modifier) {
        PortalCalculatorTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "COORDINATE CONVERSION",
                        textAlign = TextAlign.Center,
                        lineHeight = 50.sp,
                        fontSize = 50.sp
                    )
                    Spacer(
                        Modifier.height(50.dp)
                    )
                    Column {
                        Text("Enter Galactic Coordinates")
                        GalacticAddressTextField() {
                            viewModel.galacticAddress.value = it
                        }
                    }
                    Spacer(
                        Modifier.height(50.dp)
                    )
                    if(viewModel.galacticAddress.value.length == 16) {
                        Text(viewModel.galacticAddress.value)
                    }
                }
            }
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Preview(showBackground = true)
    @Composable
    fun MainContentPreview() {
        viewModel = CoordinateConversionViewModel(
            galacticAddress = mutableStateOf("0123456789abcdef")
        )
        MainContent()
    }

    @Composable
    fun GalacticAddressTextField(onChange: (String) -> Unit) {
        val textFieldValue = remember {mutableStateOf(TextFieldValue())}
        OutlinedTextField(
            value = textFieldValue.value,
            placeholder = { Text("0123:4567:89AB:CDEF") },
            onValueChange = { change ->
                val deformatted = change.text.uppercase()
                    .filter { it.isDigit() || (it.isLetter() && (it < 'G')) }
                    .take(16).also {
                        onChange(it)
                    }
                val reformatted = deformatted.let {
                    var retval = ""
                    for(i in it.indices) {
                        retval += it[i]
                        if((i + 1) % 4 == 0 && i != 15) retval += ":"
                    }
                    retval
                }
                textFieldValue.value = change.copy(
                    reformatted,
                    if(change.selection.start == change.text.length && change.text.length >= textFieldValue.value.text.length) TextRange(reformatted.length) else change.selection
                )
            }
        )
    }
}

data class CoordinateConversionViewModel(
    val galacticAddress: MutableState<String> = mutableStateOf("")
) : ViewModel()