/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package obsolete.uit.tkorg.paperrecommender.utility.importdata;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Minh
 * Tool import data tien xu li
 * Ý tưởng của em là đầu tiên em đọc data vào một cái bảng em tao riêng ở CSDL ben ngoai
 * sao khi import data xong em được một cái bảng bao gồm idPaper, keyword, trọng số,
 * Xong em dùng lệnh của sql
 *             Select distinct trong sql em lọc ra được idPaper, tập các từ vựng,
 *             từ sql em lưu xuống file txt, từ file txt đó em dùng sql đọc đọc lên bảng luôn mà
 *             không có code. em có gủi kèm 2 file data em xử lí được, trong docment.
*              nhưng mà lúc em dùng hàm builtPaper_keyword() đọc data vào bảng paper_key word thì đọc giữa chừng nó
*              báo lỗi 
 */
public class Importdata {
      Mysqlconnection msc;

    /**
     * @param args the command line arguments
     */
  //  private int idkey_word =0;
    public static void main(String[] args) throws IOException, SQLException {
        Importdata t = new Importdata();
        t.msc = new Mysqlconnection();
        t.msc.connectDb();
        //  t.builtCitationNetwork();
        // t.builtPaper_keyword();
        String dirToRecurse = "E:\\Luan van\\20100825-SchPaperRecData\\";
         try {
         t.fileList(new File(dirToRecurse));
         } catch (FileNotFoundException ex) {
         Logger.getLogger(Importdata.class.getName()).log(Level.SEVERE, null, ex);
         }
         t.msc.closeConnect();   
    }
    // Ham import data vào mạng trích dẫn
     /* public void builtCitationNetwork() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""E:\\Luan van\\20100825-SchPaperRecData\\InterLink\\acl.20080325.net"));
        try {
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                if (line != null) {
                    // code here
                    String[] str = line.split(" ==> ");
                    if (str.length == 2) {
                        this.msc.insertDatabase(str[0], str[1]);
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("0000 " + e.getMessage());
        } finally {
            br.close();
        }
    }*/
                // Ham chen du lieu vao bang paper_keyword
    
    /* public void builtPaper_keyword() throws FileNotFoundException, IOException {
       BufferedReader br = new BufferedReader(new FileReader("E:\\ Dataset1\\paper_keyword"));
        try {
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                if (line != null) {
                    // code here
                    String[] str = line.split("\t");
                    if (str.length == 3) {
                        int rowid.this.msc.returnid(str[1]);
                        if (rowid==0) 
                        break;
                        else
                        this.msc.insertDatabase(str[0],rowid,Double.parseDouble (str[2]));
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("0000 " + e.getMessage());
        } finally {
            br.close();
        }
    }*/
        // Ham import data 
        public void fileList(File dir) throws FileNotFoundException, IOException, SQLException {
            //Lay danh sach tat cac cac file co trong thu muc
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                // Duyet tim file import data
                if (files[i].isDirectory()) 
                {
                    fileList(files[i]);
                } else {
                    System.out.println(files[i].getAbsolutePath());

                    if (files[i].getAbsolutePath().indexOf(".txt") != -1) {
                        BufferedReader br = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
                    try {
                        String line = br.readLine();
                        while (line != null) {
                            line = br.readLine();
                            if (line != null) {
                                String[] str = line.split(" ");
                                if (str.length == 2) {
                              // idkey_word = Integer.valueOf(str[0]);
                              //  this.msc.insertDatabase(idkey_word, str[1]);
                                this.msc.insertDatabase(files[i].getName().replaceAll("_fv.txt|_recfv.txt", ""),str[0],str[1]);

                                } else {
                                    break;
                                }
                            }
                        }
                        } catch (Exception e) {
                            System.out.println("0000 "+e.getMessage());
                        } finally {
                            br.close();
                        }
                    }
                }
            }
    }
    
}
