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
                "      \"test_id\": \"0\",\n" +
                "      \"title\": \"Подберите пару для графика\",\n" +
                "      \"question\": {\n" +
                "        \"picture\": \"graph_x_1\",\n" +
                "        \"correct_id\": \"1\"\n" +
                "      },\n" +
                "      \"answers\": [\n" +
                "        {\n" +
                "          \"id\": \"1\",\n" +
                "          \"picture_name\": \"graph_v_1\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"2\",\n" +
                "          \"picture_name\": \"graph_v_2\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"3\",\n" +
                "          \"picture_name\": \"graph_v_3\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"4\",\n" +
                "          \"picture_name\": \"graph_v_4\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"5\",\n" +
                "          \"picture_name\": \"graph_v_5\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"6\",\n" +
                "          \"picture_name\": \"graph_v_6\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"7\",\n" +
                "          \"picture_name\": \"graph_v_7\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"8\",\n" +
                "          \"picture_name\": \"graph_v_8\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"state2graph\",\n" +
                "      \"test_id\": \"1\",\n" +
                "      \"title\": \"Найдите соответсвите графиков и состояний\",\n" +
                "      \"question\": [\n" +
                "        {\n" +
                "          \"correct_id\": \"1\",\n" +
                "          \"picture_name\": \"graph_x_1\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"correct_id\": \"2\",\n" +
                "          \"picture_name\": \"graph_x_2\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"correct_id\": \"3\",\n" +
                "          \"picture_name\": \"graph_x_3\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"correct_id\": \"4\",\n" +
                "          \"picture_name\": \"graph_x_4\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"answers\": [\n" +
                "        {\n" +
                "          \"id\": \"0\",\n" +
                "          \"state\": \"Движется вперед\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"1\",\n" +
                "          \"state\": \"Движется назад\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"2\",\n" +
                "          \"state\": \"Движется назад\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"3\",\n" +
                "          \"state\": \"Движется вперед\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"4\",\n" +
                "          \"state\": \"Движется назад\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"5\",\n" +
                "          \"state\": \"Движется вперед\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"6\",\n" +
                "          \"state\": \"Движется вперед\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"7\",\n" +
                "          \"state\": \"Движется вперед\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"graph2graph2\",\n" +
                "      \"test_id\": \"test_id\",\n" +
                "      \"title\": \"Найдите для графика два других\",\n" +
                "      \"question\": {\n" +
                "        \"picture\": \"graph_x_1\",\n" +
                "        \"correct_ids\": [\n" +
                "          \"1\", \"2\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"answers\": [\n" +
                "        {\n" +
                "          \"id\": \"1\",\n" +
                "          \"picture_name\": \"graph_v_1\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"2\",\n" +
                "          \"picture_name\": \"graph_v_2\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"3\",\n" +
                "          \"picture_name\": \"graph_v_3\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"4\",\n" +
                "          \"picture_name\": \"graph_v_4\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"5\",\n" +
                "          \"picture_name\": \"graph_v_5\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"6\",\n" +
                "          \"picture_name\": \"graph_v_6\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"7\",\n" +
                "          \"picture_name\": \"graph_v_7\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"8\",\n" +
                "          \"picture_name\": \"graph_v_8\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"relationSings\",\n" +
                "      \"test_id\": \"test_id\",\n" +
                "      \"title\": \"Установите правильные неравенства\",\n" +
                "      \"task_picture\": \"screenshot1\",\n" +
                "      \"questions\": [\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"correct_sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"correct_sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"correct_sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"correct_sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"correct_sign\": \"equal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"letter\": \"S\",\n" +
                "          \"left_index\": \"01\",\n" +
                "          \"right_index\": \"12\",\n" +
                "          \"correct_sign\": \"equal\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}"
        val data = JSONObject(text.substring(text.indexOf("{"), text.lastIndexOf("}") + 1)).optJSONArray("tests")
        for(i in 0 until data!!.length())
            tests.add(data.getJSONObject(i))

    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
