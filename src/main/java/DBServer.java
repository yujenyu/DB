import core.DBContext;
import core.exception.ColumnSizeNotMatchException;
import core.exception.DataBaseExistsException;
import core.exception.DataBaseNotExistsException;
import core.exception.InvalidSqlException;
import core.exception.ParseException;
import core.exception.TableExistsException;
import core.exception.TableNotExistsException;

import java.io.*;
import java.net.*;
class DBServer
{
    final static char EOT = 4;
    public static void main(String args[]) {
        DBServer server = new DBServer(8888);
    }

    public DBServer(int portNumber)
    {
        while(true) {
            try {
                System.out.println("Server Listening");
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String line;
                DBContext ctx = new DBContext();
                while ((line = in.readLine()) != null) {
                    processNextCommand(line, out, ctx);
                }
                out.close();
                in.close();
                socket.close();
                serverSocket.close();
            } catch (IOException ioe) {
                System.err.println(ioe);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processNextCommand(String sqlInput, BufferedWriter out, DBContext ctx) throws Exception {
        try{
            DBController controller = new DBController(sqlInput, ctx);
            String output = controller.executeQuery();
            out.write(output + "\n" + EOT + "\n");
            out.flush();
        }
        catch (ColumnSizeNotMatchException |
                DataBaseExistsException |
                DataBaseNotExistsException |
                InvalidSqlException |
                ParseException |
                TableExistsException |
                TableNotExistsException e){
            out.write(e + "\n" + EOT + "\n");
            out.flush();
            e.printStackTrace();
            System.err.println(e);
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e);
        }
    }
}
