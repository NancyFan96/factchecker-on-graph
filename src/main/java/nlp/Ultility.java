package nlp;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Ultility {
    public static String URI2KeyString(String longURI) {
        String[] tokens = longURI.split("/");
        String lastname = tokens[tokens.length-1];

        return lastname;
    }

   public static String Path2NameExtend(String Path) {
        String[] tokens = Path.split("/");
        return tokens[tokens.length - 1];
   }

   public static String Path2Name(String Path) {
        String NameExtend = Path2NameExtend(Path);
        String [] tokens = NameExtend.split("\\.");
        return tokens[0];
   }

   public static String JsonTriple2String(JSONObject tripleJson) {
       String subject = tripleJson.get("subject").toString();
       String relation = tripleJson.get("predicate").toString();
       String object = tripleJson.get("object").toString();

       String tostring = subject + " " + relation + " " + object;
       return tostring;
   }




    public static void main(String[] args)  {
//        String Path = "src/main/resources/test-triple.json";
//        String FileNameExt = Path2NameExtend(Path);
//        String FileName = Path2Name(Path);

        String s = "http://dbpedia.org/resource/Dublin";
        Pattern p = Pattern.compile("http:\\/\\/dbpedia\\.org\\/resource\\/[A-Z,a-z]*_\\(disambiguation\\)");//"http://dbpedia.org/resource/*disambiguation$");
        String picked = null;


        if (p.matcher(s).find()) {
            picked = s;
            if(!URI2KeyString(s).contains(":"))
                System.out.println(picked);
        }

        s = "http://dbpedia.org/resource/Dublin_(disambiguation)";


//        s = "http://dbpedia.org/resource/Category:Dublin";


            if (p.matcher(s).find()) {
                picked = s;
                if(!URI2KeyString(s).contains(":"))
                    System.out.println(picked);
            }

        s = "hhhhh";
        if (p.matcher(s).find()) {
            picked = s;
            if(!URI2KeyString(s).contains(":"))
                System.out.println(picked);
        }




//        try {
//            // create a new writer
//            RandomAccessFile f = new RandomAccessFile(new File("testwrite.txt"), "rw");
//
//            f.seek(0); // to the beginning
//            f.write("[]".getBytes());
//            f.seek(f.length()-1); // to the beginning
//            f.write("Jennifer".getBytes());
//            f.close();
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

//        ArrayList<Integer> ValueList = new ArrayList<>();
//        ArrayList<Integer> GroupIdList = new ArrayList<>();
//        GroupIdList.add(1);
//        GroupIdList.add(1);
//        GroupIdList.add(1);
//        GroupIdList.add(2);
//        GroupIdList.add(2);
//        ValueList.add(5);
//        ValueList.add(4);
//        ValueList.add(9);
//        ValueList.add(-3);
//        ValueList.add(-7);
//
//        List<Integer> subList1 = ValueList.subList(GroupIdList.indexOf(1), GroupIdList.lastIndexOf(1)+1);
//        List<Integer> subList2 = ValueList.subList(GroupIdList.indexOf(2), GroupIdList.lastIndexOf(2)+1);



        System.out.println();

    }
}