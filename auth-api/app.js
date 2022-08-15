import express from "express";

import tracing from "./src/config/tracing.js";"./src/config/tracing.js";
import UserRoutes from "./src/modules/user/routes/UserRoutes.js";

import * as db from "./src/config/db/initialData.js";

const app = express();
const env = process.env;
const PORT = env.PORT || 8080;

db.createInitialData();
app.use(tracing);

app.get("/api/status", (req, res) => {
    return res.status(200).json({
        service: "Auth-API",
        status: "up",
        httpStatus: 200        
    });
});

app.use(express.json());
app.use(UserRoutes);

app.listen(PORT, () => {
    console.info(`Server has started succefully at port ${PORT}`);
});