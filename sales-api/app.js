import express from 'express';
import { connect } from './src/config/db/mongoDbConfig.js';

const app = express();
const env = process.env;
const PORT = env.PORT || 8082;

connect();

app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`)
});

app.get('/api/status', (req, res) => {
    return res.status(200).json({
        service: "sales-api",
        status: "up",
        httpStatus: 200,
    });
});