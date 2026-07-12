import xml.etree.ElementTree as ET
import os
import sys

def main():
    xml_path = "Backend/target/site/jacoco/jacoco.xml"
    if not os.path.exists(xml_path):
        print(f"Error: JaCoCo XML report not found at {xml_path}")
        sys.exit(0)

    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
    except Exception as e:
        print(f"Error parsing XML: {e}")
        sys.exit(0)

    summary = []
    summary.append("### 📊 Backend Code Coverage Report (JaCoCo)\n")
    summary.append("| Counter Type | Covered | Missed | Total | Coverage % | Status |")
    summary.append("|--------------|---------|--------|-------|------------|--------|")

    # Find the top-level counters (overall project stats)
    counters = root.findall("counter")
    for counter in counters:
        c_type = counter.get("type")
        missed = int(counter.get("missed", 0))
        covered = int(counter.get("covered", 0))
        total = missed + covered
        percentage = (covered / total * 100) if total > 0 else 0.0
        
        # Add visual status indicator
        status = "🟢 Excellent" if percentage >= 80 else ("🟡 Warning" if percentage >= 50 else "🔴 Low")
        summary.append(f"| {c_type} | {covered} | {missed} | {total} | {percentage:.2f}% | {status} |")

    # Write to GitHub Actions step summary if available, otherwise stdout
    summary_text = "\n".join(summary) + "\n"
    summary_file = os.getenv("GITHUB_STEP_SUMMARY")
    if summary_file:
        with open(summary_file, "a") as f:
            f.write(summary_text)
    else:
        print(summary_text)

if __name__ == "__main__":
    main()
