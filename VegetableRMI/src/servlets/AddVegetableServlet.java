package servlets;

import client.AddVegetablePrice;
import common.Compute;
import common.VegetablePrice;
import server.VegetableComputeEngine;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * AddVegetableServlet - receives HTTP POST with vegetable data,
 * creates an AddVegetablePrice task, and sends it to the RMI engine.
 *
 * URL: POST /vegetable/add
 * Params: id, name, price
 *
 * DIARY: Day 4 - Created the servlet layer.
 *        Each servlet gets a Compute stub from the RMI registry,
 *        builds the appropriate Task, calls executeTask(), and
 *        writes the result back as plain text to the HTTP response.
 */
@WebServlet("/vegetable/add")
public class AddVegetableServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String id    = req.getParameter("id");
        String name  = req.getParameter("name");
        String price = req.getParameter("price");

        if (id == null || name == null || price == null) {
            resp.setStatus(400);
            out.println("ERROR: Missing parameters. Required: id, name, price");
            return;
        }

        try {
            double priceVal = Double.parseDouble(price);
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Compute engine = (Compute) registry.lookup(VegetableComputeEngine.REGISTRY_NAME);

            String result = engine.executeTask(
                    new AddVegetablePrice(new VegetablePrice(id, name, priceVal)));

            out.println(result);
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            out.println("ERROR: Price must be a valid number.");
        } catch (Exception e) {
            resp.setStatus(500);
            out.println("ERROR: " + e.getMessage());
        }
    }
}
