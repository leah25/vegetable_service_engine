package servlets;

import client.DeleteVegetablePrice;
import common.Compute;
import server.VegetableComputeEngine;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.rmi.registry.*;

/**
 * DeleteVegetableServlet - POST /vegetable/delete  param: id
 * DIARY: Day 4 - Servlet for delete task.
 */
@WebServlet("/vegetable/delete")
public class DeleteVegetableServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String id = req.getParameter("id");
        if (id == null) { resp.setStatus(400); out.println("ERROR: Missing id"); return; }
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            Compute engine = (Compute) reg.lookup(VegetableComputeEngine.REGISTRY_NAME);
            out.println(engine.executeTask(new DeleteVegetablePrice(id)));
        } catch (Exception e) { resp.setStatus(500); out.println("ERROR: " + e.getMessage()); }
    }
}
