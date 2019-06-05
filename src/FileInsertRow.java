import java.io.*;

/**
 * 给文件增加一行数据。
 * 
 * 
 */
public class FileInsertRow {
  public static void main(String[] args) {
    //readFileByLines("C:\\Users\\Administrator\\Desktop\\aaa.txt");
  }
    public static void readFileByLines(String fileName) {
      File file = new File(fileName);
      BufferedReader reader = null;
      try {
        System.out.println("以行为单位读取文件内容，一次读一整行：");
        reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        // 一次读入一行，直到读入null为文件结束
        int line = 0;
        String flag = "";
        while ((tempString = reader.readLine()) != null) {
          // 显示行号
         String[] a = tempString.split(",");
          FileInsertRow j = new FileInsertRow();
          if(!flag.equals(a[0])){
            flag = a[0];
            line = 0;
          }
          String newFlag = flag;
          int insertLine = Integer.parseInt(a[1]) + line;
          j.insertStringInFile(new File(a[0]),
                  insertLine, "LoggingUtils.getEvoLogger().info(\"===="+ newFlag.replaceAll("\\\\","/") +"====" + a[2] + "====="+ (insertLine - 1) +"====\");");
          line ++;
        }
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e1) {
          }
        }
      }
  }
  /**
   * 在文件里面的指定行插入一行数据
   * 
   * @param inFile
   *          文件
   * @param lineno
   *          行号
   * @param lineToBeInserted
   *          要插入的数据
   * @throws Exception
   *           IO操作引发的异常
   */
  public static void insertStringInFile(File inFile, int lineno, String lineToBeInserted) {
    // 临时文件
    File outFile = null;
    try {
      outFile = File.createTempFile("name", ".tmp");
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 输入
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(inFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(fis));
    // 输出
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(outFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    PrintWriter out = new PrintWriter(fos);
    // 保存一行数据
    String thisLine = "";
    // 行号从1开始
    int i = 1;
    while (true) {
      try {
        if (!((thisLine = in.readLine()) != null)) break;
      } catch (IOException e) {
        e.printStackTrace();
      }
      // 如果行号等于目标行，则输出要插入的数据
      if (i == lineno) {

        out.println(lineToBeInserted);
      }
      // 输出读取到的数据
      out.println(thisLine);
      // 行号增加
      i++;
    }

    try {
      out.flush();
      out.close();
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 删除原始文件
    inFile.delete();
    // 把临时文件改名为原文件名
    outFile.renameTo(inFile);
  }
}
