package servlets;

import client.CalVegetableCost;
import client.CalculateCost;
import common.Compute;
import server.VegetableComputeEngine;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.rmi.registry.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CalCostServlet - POST /vegetable/cost
 * Params: id, quantity
 * DIARY: Day 4 - Servlet to calculate cost for a single vegetable.
 */
@WebServlet("/vegetable/cost")
class CalCostServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String id = req.getParameter("id"), qty = req.getParameter("quantity");
        if (id == null || qty == null) {
            resp.setStatus(400); out.println("ERROR: Missing id or quantity"); return;
        }
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            Compute engine = (Compute) reg.lookup(VegetableComputeEngine.REGISTRY_NAME);
            out.println(engine.executeTask(new CalVegetableCost(id, Double.parseDouble(qty))));
        } catch (Exception e) { resp.setStatus(500); out.println("ERROR: " + e.getMessage()); }
    }
}

/**
 * ReceiptServlet - POST /vegetable/receipt
 * Params: cashier, amountGiven, items (format: "V001:2.5,V002:1.0")
 * DIARY: Day 4 - Servlet for full receipt generation.
 *        Items are sent as a comma-separated string of id:qty pairs.
 */
@WebServlet("/vegetable/receipt")
class ReceiptServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String cashier     = req.getParameter("cashier");
        String amountStr   = req.getParameter("amountGiven");
        String itemsStr    = req.getParameter("items"); // e.g. "V001:2.5,V002:1.0"

        if (cashier == null || amountStr == null || itemsStr == null) {
            resp.setStatus(400);
            out.println("ERROR: Missing cashier, amountGiven, or items");
            return;
        }

        try {
            // Parse items string into map
            Map<String, Double> items = new LinkedHashMap<>();
            for (String pair : itemsStr.split(",")) {
                String[] kv = pair.split(":");
                items.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
            }

            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            Compute engine = (Compute) reg.lookup(VegetableComputeEngine.REGISTRY_NAME);
            out.println(engine.executeTask(
                    new CalculateCost(items, Double.parseDouble(amountStr), cashier)));
        } catch (Exception e) { resp.setStatus(500); out.println("ERROR: " + e.getMessage()); }
    }
}
