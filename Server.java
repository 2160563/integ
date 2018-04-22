package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final int PORT = 9001;

    private static ArrayList<String> names = new ArrayList<String>();

    private static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();

    private static ArrayList<String> list = new ArrayList<String>();

    private static ArrayList<String> score = new ArrayList<String>();
    
    static int num = 0;
    
    static int n = 1;
    
    public static void main(String[] args) throws Exception {
        list.add("The Way Get Started Is To Quit Talking And Begin Doing.");
        list.add("The Pessimist Sees Difficulty In Every Opportunity. The Optimist Sees Opportunity In Every Difficulty.");
        list.add("Don’t Let Yesterday Take Up Too Much Of Today.");
        list.add("You Learn More From Failure Than From Success. Don’t Let It Stop You. Failure Builds Character.");
        list.add("It’s Not Whether You Get Knocked Down, It’s Whether You Get Up.");
        list.add("If You Are Working On Something That You Really Care About, You Don’t Have To Be Pushed. The Vision Pulls You.");
        list.add("People Who Are Crazy Enough To Think They Can Change The World, Are The Ones Who Do.");
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);

        try {
            while (true) {
                new Handler(listener.accept()).start();
                num++;
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {

        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {

                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }
                if (names.size() > 3) {
                    out.println("no cannot be");
                } else {
                    out.println("NAMEACCEPTED");
                    writers.add(out);
                }
                while (true) {
                    Map<String, Integer> counts = new HashMap<String, Integer>();

                    for (String str : score) {
                        if (counts.containsKey(str)) {
                            counts.put(str, counts.get(str) + 1);
                        } else {
                            counts.put(str, 1);
                        }
                    }

                    int i = (int) (Math.random() * 7);
                    
                    if (num != 1 && num != 0 && num % writers.size() == 0 && writers.size() == 3) {
                        writers.stream().map((writer) -> {
                            writer.println("QUESTION " + "Simon" + ": " + list.get(i));
                            return writer;
                        }).forEachOrdered((writer) -> {
                            counts.entrySet().stream().map((entry) -> {
                                writer.println("SCORE: " + entry.getKey() + " = " + entry.getValue());
                                return entry;
                            }).map((entry) -> {
                                return entry;
                            }).filter((entry) -> (entry.getValue() == 3)).forEachOrdered((entry) -> {
                                writer.println("DECISION " + entry.getKey() + " is the winner!!");
                            });
                            if(score.size()>1){
                                score.subList(1,n).clear();
                            }
                            num = 0;
                        });
                    } 
                    if(writers.size()<3) {
                        int number = 3-writers.size();
                        writers.forEach((writer) -> {
                            writer.println("WAIT " + "System" + ": Waiting for " + number + " more player/s");
                        });
                    }
                    String input = in.readLine();
                    if (input == null) {
                        input = " ";
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                    if (input.compareTo(list.get(i)) == 0) {
                        score.add(name);
                    }
                    num++;
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {

                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
