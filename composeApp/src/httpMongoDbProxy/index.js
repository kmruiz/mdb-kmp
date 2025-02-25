const express = require('express')
const mongodb = require('mongodb')
const cors = require('cors')

const app = express()
const port = 8085

/**
 * @type mongodb.MongoClient
 */
let client = null;

app.use(express.json())
app.use(cors())

app.post('/connect', async (req, res) => {
    if (client != null) {
        await client.close(true)
    }

    try {
        console.log('Connecting to', req.body.url)
        client = new mongodb.MongoClient(req.body.url)
        console.log('Sending hello command.')
        await client.db("admin").command({ "hello": 1 })
        console.log('Hello command finished successfully.')
        res.sendStatus(200)
    } catch (ex) {
        res.status(400).send({ error: ex.message })
    }
})

app.post('/disconnect', async (req, res) => {
    if (client != null) {
        void client.close(true)
        client = null
    }

    res.sendStatus(200)
})

app.get('/databases', async (req, res) => {
    if (client == null) {
        res.sendStatus(404)
    } else {
        const doc = await client.db("admin").command({ listDatabases: 1 })
        const dbs = doc["databases"];
        const result = dbs.map(d => ({ name: d.name, size: d.sizeOnDisk }))
        res.send(result)
    }
})

app.listen(port, () => {
    console.log(`MongoDB Http Proxy running at port ${port}`)
})
