package de.uol.helloresolver

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.uol.helloresolver.ui.theme.HelloResolverTheme
import edu.ucsd.sccn.LSL

class MainActivity : ComponentActivity() {
    private val wifiManager: WifiManager get() = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var defaultColor=MaterialTheme.colorScheme.background
            var surfaceColor = remember { mutableStateOf(defaultColor) }
            var buttonColor = remember { mutableStateOf(Color.Red) }
            var buttonText = remember { mutableStateOf("TEST") }
            HelloResolverTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = surfaceColor.value
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val buttonColors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan, contentColor = Color.DarkGray
                        )
                        Button(
                            onClick = {
                                var lock = wifiManager.createMulticastLock("MC Lock")
                                lock.acquire()

                                var stream1 =
                                    LSL.StreamOutlet(LSL.StreamInfo("TestStream1", "other"))
                                stream1.push_chunk(FloatArray(1))

                                var stream2 =
                                    LSL.StreamOutlet(LSL.StreamInfo("TestStream2", "other"))
                                stream2.push_chunk(FloatArray(1))


                                var streams = LSL.resolve_streams()
                                var str = "Found ${streams.size} streams:"
                                for (s in streams) {
                                    str += " ${s.name()}"
                                }
                                if (streams.any { it ->
                                        it.name().equals("TestStream1")
                                    } && streams.any { it -> it.name().equals("TestStream2") }) {
                                    Toast.makeText(applicationContext, "PASS", Toast.LENGTH_LONG)
                                        .show()
                                    surfaceColor.value=Color.Green

                                } else {
                                    Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_LONG)
                                        .show()
                                    surfaceColor.value=Color.Red
                                }
                                Log.e("HelloResolver", str)
                                lock.release()
                                stream1.close()
                                stream2.close()
                            },
                            modifier = Modifier
                                .size(width = 100.dp, height = 100.dp)
                                .background(color = Color.Unspecified, shape = CircleShape)
                        ) {
                            Text(text = buttonText.value)
                        }
                    }
                }
            }
        }
    }
}