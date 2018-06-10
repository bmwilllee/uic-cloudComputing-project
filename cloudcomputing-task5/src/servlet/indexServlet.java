package servlet;

import app.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class indexServlet
 */
@WebServlet("/indexServlet")
public class indexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	ArrayList<Merge> resultList;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public indexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String msg = "";
		String keyword = request.getParameter("keyword");
		resultList = Search.search(keyword);
		
		for (Merge merge : resultList) {
			String lineList = "";
			for (String line : merge.lineList) {
				lineList += "      Line: " + line;
			}
			msg += "<div class=\"col-md-12 col-lg-12 col-xs-12 col-sm-12\">\n" + 
					"					<strong><a href=\"#\">" + merge.getFileName() + "</a></strong>\n" + 
					"					<h5>Total: " + merge.num + "</h5>\n" + 
					"					<div>\n" +
					"						<p class=\"text-left muted\">" + lineList + "</p>\n" +
					"					</div>\n" + 
					"				</div>";
		}
		request.setAttribute("msg", msg);
		request.getRequestDispatcher("Index.jsp").forward(request, response);
	}

}
