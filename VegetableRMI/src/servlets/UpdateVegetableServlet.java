// ─────────────────────────────────────────────────────────────────────────────
// UpdateVegetableServlet.java
// PUT /vegetable/update  params: id, name, price
// ─────────────────────────────────────────────────────────────────────────────
package servlets;

import client.UpdateVegetablePrice;
import common.Compute;
import common.VegetablePrice;
import server.VegetableComputeEngine;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.rmi.registry.*;

@WebServlet("/vegetable/update")
public class UpdateVegetableServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String id = req.getParameter("id"), name = req.getParameter("name"),
               price = req.getParameter("price");
        if (id == null || name == null || price == null) {
            resp.setStatus(400); out.println("ERROR: Missing id, name, or price"); return;
        }
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            Compute engine = (Compute) reg.lookup(VegetableComputeEngine.REGISTRY_NAME);
            out.println(engine.executeTask(
                    new UpdateVegetablePrice(new VegetablePrice(id, name, Double.parseDouble(price)))));
        } catch (Exception e) { resp.setStatus(500); out.println("ERROR: " + e.getMessage()); }
    }
}
