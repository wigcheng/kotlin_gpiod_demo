package com.example.gpiod

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gpiod.ui.theme.GpiodTheme

private const val TAG = "MyActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GpiodTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                ) {
                    var DropdownGpioBank = createDropDownList()
                    var DropdownGpioLine = createDropDownList()
                    var DropdownGpioValue = createDropDownList()

                    Column {
                        CardDemo()
                        Greeting("Select GPIO BANK")
                        DropdownGpioBank.DropdownCreate( getGpioTotalBank(), createDropDownList.listType.GPIO_BANK)
                        Greeting("Select GPIO LINE (Type number)")
                        DropdownGpioLine.gpioLineTextField()
                        Greeting("Select GPIO Value (OUTPUT only)")
                        DropdownGpioValue.DropdownCreate(2, createDropDownList.listType.GPIO_VALUE)
                        Row (horizontalArrangement = Arrangement.spacedBy(8.dp),){
                            ButtonGet(DropdownGpioBank, DropdownGpioLine)
                            ButtonSet(DropdownGpioBank, DropdownGpioLine, DropdownGpioValue)
                        }
                    }
                }
            }
        }
    }

    external fun stringFromJNI(): String
    external fun getGpioTotalBank(): Int
    external fun setGpioInfo(bank: Int, line: Int, value: Int): String
    external fun getGpioInfo(bank: Int, line: Int): String

    companion object {
        // Used to load the 'myapplication' library on application startup.
        init {
            System.loadLibrary("JNIGpiod")
        }
    }
}

class createDropDownList{

    var global_index = 0

    enum class listType {
        GPIO_BANK,
        GPIO_LINE,
        GPIO_VALUE,
    }

    @Composable
    fun gpioLineTextField() {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                global_index = text.text.toString().toInt()
            },
            modifier = Modifier.fillMaxWidth().background(Color.White)
        )
    }

    @Composable
    fun DropdownCreate(total: Int, dropdown_type: listType) {
        var expanded by remember { mutableStateOf(false) }

        var selectedIndex by remember { mutableStateOf(0) }
        var type_str: String = ""
        val items = ArrayList<String>()

        if(dropdown_type == listType.GPIO_BANK) {
            type_str = "gpiochip"
        }else if(dropdown_type == listType.GPIO_LINE) {
            type_str = "Num "
        }

        if(dropdown_type == listType.GPIO_VALUE) {
            items.add("0")
            items.add("1")
        } else {
            for (num in 1..total) {
                items.add(type_str + (num - 1))
            }
        }

        val disabledValue = "gpiochip6"
        Row {
            Box(modifier = Modifier
                .size(45.dp).clickable(onClick = { expanded = true })
                .clip(RectangleShape).background(Color.Gray)) {
                Image(painter = painterResource(R.drawable.ic_down_chevron), contentDescription = "down_chevron")
            }

            Text(
                items[selectedIndex],
                modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = true })
                    .background(Color.Gray),
                fontSize = 30.sp
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth().background(Color.White),
            ) {
                items.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        expanded = false
                        global_index = selectedIndex
                        Log.i(TAG, "global_index = " + global_index)
                    }) {
                        val disabledText = if (s == disabledValue) {
                            " (Disabled)"
                        } else {
                            ""
                        }
                        Text(text = s + disabledText, fontSize = 30.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CardDemo() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable{ },
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                buildAnnotatedString {
                    append("welcome to ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)
                    ) {
                        append("libgpiod base GPIO control panel")
                    }
                },fontSize = 30.sp
            )
        }
    }
}

@Composable
fun Greeting(name: String) {
    Row {
        Text(text = "$name", fontSize = 30.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GpiodTheme {
        Greeting("Android")
    }
}

@Composable
fun ButtonSet(targetBank: createDropDownList, targetLine: createDropDownList, targetValue: createDropDownList){
    val context = LocalContext.current
    Button(onClick = {
        //your onclick code
        //Log.i(TAG, "JNI SET ret = " + MainActivity().setGpioInfo(targetBank.global_index, targetLine.global_index, targetValue.global_index))
        Toast.makeText(
            context,
            MainActivity().setGpioInfo(targetBank.global_index, targetLine.global_index, targetValue.global_index),
            Toast.LENGTH_SHORT
        ).show()
    },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray))

    {
        Text(text = "Set (OUTPUT)",color = Color.White,  fontSize = 30.sp)
    }
}

@Composable
fun ButtonGet(targetBank: createDropDownList, targetLine: createDropDownList){
    val context = LocalContext.current
    Button(onClick = {
        //your onclick code
        //Log.i(TAG, "JNI GET ret = " + MainActivity().getGpioInfo(targetBank.global_index, targetLine.global_index))
        Toast.makeText(
            context,
            "Get Vaule = " + MainActivity().getGpioInfo(targetBank.global_index, targetLine.global_index),
            Toast.LENGTH_SHORT
        ).show()
    },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),)

    {
        Text(text = "Get (INPUT)",color = Color.White,  fontSize = 30.sp)
    }
}