import json
import sys
import re

def parse_osu(osu_path, lanes=4, approach=2.0):
    with open(osu_path, "r", encoding="utf-8") as f:
        data = f.read()

    # Ambil section [HitObjects]
    hitobj = data.split("[HitObjects]")[-1].strip().split("\n")

    notes = []
    lane_width = 512 / lanes

    for line in hitobj:
        parts = line.split(",")
        if len(parts) < 5:
            continue

        x = int(parts[0])
        time = int(parts[2]) / 1000.0  # ms → s
        type_flag = int(parts[3])

        lane = int(x / lane_width)
        ntype = "tap"

        length = 0.0

        # Hold note detection
        if type_flag & 128:
            # contoh param: 1569:0:0:0:
            objectParams = parts[5]
            m = re.match(r"(\d+):", objectParams)
            if m:
                endTime = int(m.group(1)) / 1000.0
                length = endTime - time
                ntype = "hold"

        notes.append({
            "time": round(time, 6),
            "lane": lane,
            "type": ntype,
            "length": round(length, 6)
        })

    # susun beatmap JSON
    beatmap = {
        "lanes": lanes,
        "approachTime": approach,
        "notes": notes
    }

    out_path = osu_path.replace(".osu", ".json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(beatmap, f, indent=2)

    print("Converted →", out_path)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python osu_to_json.py file.osu")
        sys.exit()
    parse_osu(sys.argv[1])
