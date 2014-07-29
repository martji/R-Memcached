package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import dao.RMemcachedServer;

@SuppressWarnings("serial")
public class GetInfoServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public GetInfoServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		int type = request.getParameterMap().size();
		switch (type) {
		case 5:{
			System.out.println("Start Run!!!");
			String[] args = new String[5];
			for (int i = 1; i <= args.length; i++) {
				args[i-1] = request.getParameter("para"+i);
				System.out.print("I = " + args[i-1] + "; ");
			}
			System.out.println();
			RMemcachedServer.run(args);
			while (!RMemcachedServer.status) {
				try {
					Thread.sleep(10);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Finish Run!!!");
			out.println("finished");
			out.flush();
			out.close();
		}
			break;
		case 1:{
			while (!RMemcachedServer.initFlag && !RMemcachedServer.status) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Get Result!!!");
			String result = RMemcachedServer.getResult().toString();
			out.println(result);
			out.flush();
			out.close();
		}
			break;
		case 2:{
			float time = RMemcachedServer.time / 1000000000.0f;
			JSONArray stats = RMemcachedServer.nodeStats;
			Map <String, String> timeandStats = new HashMap<String, String>();
			timeandStats.put("time", String.format("%.3f", time));
			timeandStats.put("stats", stats.toString());
			JSONArray jsons = new JSONArray();
			jsons.put(timeandStats);
			String result = jsons.toString();
			//System.out.println(result);
			out.println(result);
			out.flush();
			out.close();
		}
		default:
			break;
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
