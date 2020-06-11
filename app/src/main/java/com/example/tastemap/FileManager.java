package com.example.tastemap;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileManager
{
    Context context;
    final String FILE_NAME = "data.txt";
    public FileManager(Context context)
    {
        this.context = context;
    }
    public void dataAdd(ListData savedData)
    {
        File file = new File(context.getFilesDir().getPath()+"/"+FILE_NAME);
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
            PrintWriter writer = new PrintWriter(bw, true);

            String information =
                      savedData.getName()+ "\t"
                    + savedData.getLatitude() + "\t"
                    + savedData.getLongitude() + "\t"
                    + savedData.getAddress() + "\t"
                    + savedData.getRate()+ "\t"
                    + savedData.getMemo()+"\n";
                            writer.print(information);
                            writer.flush();
                            writer.close();
            Log.d("TEST", "dataAdd: 저장 완료");

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFile(ArrayList<ListData> dataList){
        File file = new File(context.getFilesDir(), FILE_NAME);
        FileReader fr = null;
        BufferedReader bufrd = null;
        String str;

        if(file.exists()){
            try{
                fr = new FileReader(file);
                bufrd = new BufferedReader(fr);

                while((str = bufrd.readLine()) != null){
                    Log.d("TEST", "loadFile: "+str);
                    String temp[] = new String[6];
                    temp = str.split("\t");

                    if(temp.length == 5){
                        dataList.add(new ListData(temp[0], Double.parseDouble(temp[1]),
                                Double.parseDouble(temp[2]), temp[3],
                                Integer.parseInt(temp[4])));

                    }else {
                        dataList.add(new ListData(temp[0], Double.parseDouble(temp[1]),
                                Double.parseDouble(temp[2]), temp[3],
                                Integer.parseInt(temp[4]), temp[5]));
                    }
                }
                Log.d("TEST", "loadFile: 저장완료");
                Log.d("TEST", "loadFile: 저장된 파일 " +dataList);

                bufrd.close();
                fr.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void dataUpdate(ArrayList<ListData> updateData)
    {
        File file = new File(context.getFilesDir().getPath(),FILE_NAME);
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,false));
            PrintWriter writer = new PrintWriter(bw, true);
            for(int i=0;i<updateData.size();i++)
            {
                ListData updatedString = updateData.get(i);
                String information = updatedString.toString();
                Log.d("test", "dataUpdate:정보 " +information);
                writer.print(information);
                writer.flush();

            } writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
