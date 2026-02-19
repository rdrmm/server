from fastapi import FastAPI, Request
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
import psycopg2
import os

app = FastAPI(title="rdRMM UI", description="Web UI for rdRMM heartbeat monitoring")

# Mount static files
app.mount("/static", StaticFiles(directory="static"), name="static")

# Templates
templates = Jinja2Templates(directory="templates")

def get_db_connection():
    return psycopg2.connect(
        host="postgres",
        database="rdrmm",
        user="user",
        password="pass"
    )

@app.get("/", response_class=HTMLResponse)
async def read_root(request: Request):
    heartbeats = []
    total_cpu = 0
    total_mem = 0
    count = 0
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT agent_uuid, hostname, cpu, mem, disk_json, timestamp FROM heartbeats ORDER BY timestamp DESC")
        rows = cur.fetchall()
        for row in rows:
            hb = {
                "agent_uuid": row[0],
                "hostname": row[1],
                "cpu": row[2],
                "mem": row[3],
                "disk": row[4],  # This is JSON string
                "timestamp": str(row[5])
            }
            heartbeats.append(hb)
            total_cpu += row[2]
            total_mem += row[3]
            count += 1
        cur.close()
        conn.close()
    except Exception as e:
        print(f"Error fetching heartbeats: {e}")
    
    avg_cpu = round(total_cpu / count, 1) if count > 0 else 0
    avg_mem = round(total_mem / count, 1) if count > 0 else 0
    last_update = heartbeats[0]['timestamp'] if heartbeats else "Never"
    
    return templates.TemplateResponse("index.html", {
        "request": request, 
        "heartbeats": heartbeats,
        "avg_cpu": avg_cpu,
        "avg_mem": avg_mem,
        "last_update": last_update
    })

@app.get("/api/heartbeats")
async def get_heartbeats():
    heartbeats = []
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT agent_uuid, hostname, cpu, mem, disk_json, timestamp FROM heartbeats ORDER BY timestamp DESC")
        rows = cur.fetchall()
        for row in rows:
            heartbeats.append({
                "agent_uuid": row[0],
                "hostname": row[1],
                "cpu": row[2],
                "mem": row[3],
                "disk": row[4],
                "timestamp": str(row[5])
            })
        cur.close()
        conn.close()
    except Exception as e:
        print(f"Error fetching heartbeats: {e}")
    return heartbeats