package com.example.physmin.activities

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.physmin.R
import com.example.physmin.fragments.FragmentTestHello
import com.example.physmin.fragments.tests.*
import org.json.JSONObject

class TestActivity : AppCompatActivity(),
        FragmentTestHello.OnFragmentInteractionListener,
        FragmentTestGraph2State.OnFragmentInteractionListener,
        FragmentTestGraph2Graph2.OnFragmentInteractionListener,
        FragmentTestSign2Relation.OnFragmentInteractionListener,
        FragmentTestGraph2Graph.OnFragmentInteractionListener
{

    var tests = arrayListOf<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val text = "{\n" +
                "  \"tests\": [\n" +
                "    {\n" +
                "      \"type\": \"graph2graph\",\n" +
                "      \"test_id\": \"test_id\",\n" +
                "      \"title\": \"Подберите пару для графа\",\n" +
                "      \"question\": {\n" +
                "        \"picture\": \"picture_name\",\n" +
                "        \"answers\": [\n" +
                "          \"id_1\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"answers\": [\n" +
                "        {\n" +
                "          \"id\": \"1\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"2\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"3\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"4\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"5\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"6\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"7\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"8\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"state2graph\",\n" +
                "      \"test_id\": \"test_id\",\n" +
                "      \"title\": \"Найдите соответсвите графиков и состояний\",\n" +
                "      \"question\": [\n" +
                "        {\n" +
                "          \"answer_id\": \"1\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"answer_id\": \"2\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"answer_id\": \"3\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"answer_id\": \"4\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"answers\": [\n" +
                "        {\n" +
                "          \"id\": \"1\",\n" +
                "          \"state\": \"Движется ВПЕРДЕ)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"2\",\n" +
                "          \"state\": \"Движется ВПЕРДЕ)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"3\",\n" +
                "          \"state\": \"Движется ВПЕРДЕ)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"4\",\n" +
                "          \"state\": \"Движется ВПЕРДЕ)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"5\",\n" +
                "          \"state\": \"Движется ВПЕРДЕ)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"6\",\n" +
                "          \"state\": \"Движется ВПЕРДЕ)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"7\",\n" +
                "          \"state\": \"Движется ВПЕРДЕ)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"8\",\n" +
                "          \"state\": \"Движется ВПЕРДЕ)\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"graph2graph2\",\n" +
                "      \"test_id\": \"test_id\",\n" +
                "      \"title\": \"Найдите для графика два других\",\n" +
                "      \"question\": {\n" +
                "        \"picture\": \"picture_name\",\n" +
                "        \"answers\": [\n" +
                "          \"id_1\",\n" +
                "          \"id_2\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"answers\": [\n" +
                "        {\n" +
                "          \"id\": \"1\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"2\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"3\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"4\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"5\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"6\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"7\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"8\",\n" +
                "          \"picture_name\": \"picture_name\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"relationSings\",\n" +
                "      \"test_id\": \"test_id\",\n" +
                "      \"title\": \"Установите правильные неравенства\",\n" +
                "      \"task_picture\": \"picture_name\",\n" +
                "      \"questions\": [\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"sign\": \"equal\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}"
        val data = JSONObject(text.substring(text.indexOf("{"), text.lastIndexOf("}") + 1)).optJSONArray("tests")
        for(i in 0..data!!.length() - 1){
            tests.add(data.getJSONObject(i))
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
