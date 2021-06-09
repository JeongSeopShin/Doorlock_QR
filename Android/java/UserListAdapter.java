package com.example.loginregister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

public class UserListAdapter extends BaseAdapter {    //extends AppCompatActivity   BaseAdapter
    //user.xml에 대한 리스트뷰를 만들었기 때문에 어댑터도 필요하다.

    private Context context;
    private List<User> userList;
    private Activity parentActivity;
    private List<User> saveList;

    public UserListAdapter(Context context, List<User> userList, Activity parentActivity,List<User> saveList) {
        //생성자이다.
        this.context = context;
        this.userList = userList;
        this.parentActivity = parentActivity;
        this.saveList = saveList;
    }

    @Override
    public int getCount() {
        return userList.size(); //현재 사용자의 갯수를 반환
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i); //유저 리스트에 특정한 유저를 반환
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        //하나의 사용자에 대한 뷰를 보여주는 메소드
        final View v = View.inflate(context, R.layout.user, null);
        final TextView userID = (TextView) v.findViewById(R.id.userID);
        TextView userPassword = (TextView) v.findViewById(R.id.userPassword);
        TextView userName = (TextView) v.findViewById(R.id.userName);
        TextView userAge = (TextView) v.findViewById(R.id.userAge);

        userID.setText(userList.get(i).getUserID()); //userID 값을 어디서 가져오는지 설정
        userPassword.setText(userList.get(i).getUserPassword());
        userName.setText(userList.get(i).getUserName());
        userAge.setText(userList.get(i).getUserAge());

        v.setTag(userList.get(i).getUserID()); //특정 유저의 ID값을 그대로 반환함.


        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
        //회원 탈퇴 버튼 이벤트 추가하기 위한 FindViewById

        deleteButton.setOnClickListener(new View.OnClickListener() {
            //회원 탈퇴 버튼을 클릭 했을 때 이벤트
            AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity); // Builder(v.getContext())
            @Override
            public void onClick(final View view) {
                builder.setTitle("안내") //대화상자 상단의 제목
                        .setMessage("정말로 삭제 하시겠습니까?") //대화상자 안의 내용
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    //결과 값을 받을 수 있는 리스너 생성
                                    @Override
                                    public void onResponse(String response) {
                                        // response가 전달되었을 때 = 특정 웹사이트로부터 response가 전달되었을 때
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            // response 값을 이렇게 받는다.
                                            boolean success = jsonResponse.getBoolean("success");
                                            if (success) {
                                                // response에서 받아온 값이 success라면
                                                userList.remove(i);
                                                //userList에서 지워준다.
                                                for (int i = 0; i < saveList.size(); i++)
                                                {
                                                    if (saveList.get(i).getUserID().equals(userID.getText().toString()))
                                                    {
                                                        saveList.remove(i);
                                                        break;
                                                    }
                                                }
                                                notifyDataSetChanged();
                                                // 데이터가 변경되었다는 것을 어댑터에 알려준다.
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                DeleteRequest deleteRequest = new DeleteRequest(userID.getText().toString(), responseListener);
                                //DeleteReques 첫번째 매개변수는 현재 디자인에 있는 userID를 그대로 보내준다.
                                RequestQueue queue = Volley.newRequestQueue(parentActivity);
                                queue.add(deleteRequest);
                                //큐에 해당 deleteRequest를 등록해주면 된다.
                            }
                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        return v;
    }
}
