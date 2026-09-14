package com.example.service;

import com.example.model.Role;
import com.example.model.User;

import org.json.JSONArray;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AudienceEngine {

    private AudienceEngine() {
        // Utility class
    }

    public static final List<Map.Entry<String, String>> INITIAL_DEPARTMENTS = Collections.unmodifiableList(Arrays.asList(
            new AbstractMap.SimpleEntry<>("CSE", "Computer Science & Engineering"),
            new AbstractMap.SimpleEntry<>("CSE (Cyber Security)", "CSE - Cyber Security"),
            new AbstractMap.SimpleEntry<>("CSE (AI & ML)", "CSE - Artificial Intelligence & ML"),
            new AbstractMap.SimpleEntry<>("AI & DS", "Artificial Intelligence & Data Science"),
            new AbstractMap.SimpleEntry<>("CSBS", "Computer Science & Business Systems"),
            new AbstractMap.SimpleEntry<>("Information Technology", "Information Technology"),
            new AbstractMap.SimpleEntry<>("ECE", "Electronics & Communication Engineering"),
            new AbstractMap.SimpleEntry<>("ECE (ACT)", "ECE - Advanced Communication Tech"),
            new AbstractMap.SimpleEntry<>("EEE", "Electrical & Electronics Engineering"),
            new AbstractMap.SimpleEntry<>("VLSI", "VLSI Design & Technology"),
            new AbstractMap.SimpleEntry<>("Biomedical", "Biomedical Engineering"),
            new AbstractMap.SimpleEntry<>("Mechanical", "Mechanical Engineering"),
            new AbstractMap.SimpleEntry<>("Mechatronics", "Mechatronics Engineering"),
            new AbstractMap.SimpleEntry<>("Civil", "Civil Engineering")
    ));

    public static final List<String> STANDARD_YEARS = Collections.unmodifiableList(Arrays.asList(
            "1st Year", "2nd Year", "3rd Year", "4th Year"
    ));

    public static final List<String> STANDARD_SECTIONS = Collections.unmodifiableList(Arrays.asList(
            "Section A", "Section B", "Section C"
    ));

    /**
     * Compute the list of authorized target tokens for a student.
     * E.g. for CSE / 2nd Year / Section A:
     * ["ALL", "CSE", "CSE|2nd Year", "CSE|2nd Year|Section A"]
     */
    public static Set<String> computeAuthorizedHierarchy(User user) {
        Set<String> tokens = new HashSet<>();
        tokens.add("ALL");
        if (user == null) {
            return tokens;
        }
        String dept = user.getDepartment() != null ? user.getDepartment().trim() : "";
        String year = user.getYear() != null ? user.getYear().trim() : "";
        String sec = user.getSection() != null ? user.getSection().trim() : "";

        if (!dept.isEmpty() && !dept.equalsIgnoreCase("ALL") && !dept.equalsIgnoreCase("ADMIN")) {
            tokens.add(dept);
            if (!year.isEmpty()) {
                tokens.add(dept + "|" + year);
                if (!sec.isEmpty()) {
                    tokens.add(dept + "|" + year + "|" + sec);
                }
            }
        }
        return tokens;
    }

    /**
     * Parse targetAudience JSON array string or comma-separated string into list of tokens.
     */
    public static List<String> parseTargets(String targetAudienceJson) {
        if (targetAudienceJson == null) {
            return Collections.singletonList("ALL");
        }
        String trimmed = targetAudienceJson.trim();
        if (trimmed.isEmpty() || trimmed.equals("ALL") || trimmed.equals("[\"ALL\"]")) {
            return Collections.singletonList("ALL");
        }
        try {
            if (trimmed.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(trimmed);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String item = jsonArray.optString(i);
                    if (item != null && !item.trim().isEmpty()) {
                        list.add(item.trim());
                    }
                }
                return list.isEmpty() ? Collections.singletonList("ALL") : list;
            } else {
                String[] parts = trimmed.split(",");
                List<String> list = new ArrayList<>();
                for (String part : parts) {
                    String t = part.trim();
                    if (!t.isEmpty()) {
                        list.add(t);
                    }
                }
                return list.isEmpty() ? Collections.singletonList("ALL") : list;
            }
        } catch (Exception e) {
            return Collections.singletonList("ALL");
        }
    }

    /**
     * Serialize list of targets into JSON array string.
     */
    public static String serializeTargets(List<String> targets) {
        List<String> compressed = compressTargets(targets);
        JSONArray jsonArray = new JSONArray();
        for (String target : compressed) {
            jsonArray.put(target);
        }
        return jsonArray.toString();
    }

    /**
     * Checks if a given student matches any of the targets defined in the notice.
     */
    public static boolean isStudentAuthorized(User user, String targetAudienceJson) {
        if (user == null) {
            return false;
        }
        // Super admins, admins, and faculty have institutional visibility
        if (user.getRole() != Role.STUDENT) {
            return true;
        }

        List<String> noticeTargets = parseTargets(targetAudienceJson);
        if (noticeTargets.contains("ALL")) {
            return true;
        }

        Set<String> studentAuthorizedTokens = computeAuthorizedHierarchy(user);

        for (String target : noticeTargets) {
            if (studentAuthorizedTokens.contains(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compress hierarchical target selections by removing redundant children:
     * - If "ALL" is present, return ["ALL"].
     * - If "CSE" is present, drop "CSE|2nd Year", "CSE|2nd Year|Section A", etc.
     * - If "CSE|2nd Year" is present, drop "CSE|2nd Year|Section A", etc.
     */
    public static List<String> compressTargets(Collection<String> targets) {
        if (targets == null || targets.isEmpty()) {
            return Collections.singletonList("ALL");
        }
        Set<String> raw = new HashSet<>();
        for (String t : targets) {
            if (t != null && !t.trim().isEmpty()) {
                raw.add(t.trim());
            }
        }
        if (raw.isEmpty() || raw.contains("ALL")) {
            return Collections.singletonList("ALL");
        }

        Set<String> depts = new HashSet<>();
        Set<String> years = new HashSet<>();
        Set<String> sections = new HashSet<>();

        for (String t : raw) {
            int pipeCount = countMatches(t, '|');
            if (pipeCount == 0) {
                depts.add(t);
            } else if (pipeCount == 1) {
                years.add(t);
            } else if (pipeCount == 2) {
                sections.add(t);
            }
        }

        List<String> result = new ArrayList<>();
        // 1. Add all Department level targets
        result.addAll(depts);

        // 2. Add Year level targets if its department is not already in depts
        for (String yearTarget : years) {
            String deptPart = yearTarget.contains("|") ? yearTarget.substring(0, yearTarget.indexOf('|')) : "";
            if (!depts.contains(deptPart)) {
                result.add(yearTarget);
            }
        }

        // 3. Add Section level targets if neither its department nor its year is in the list
        for (String secTarget : sections) {
            String[] parts = secTarget.split("\\|");
            String deptPart = parts.length > 0 ? parts[0] : "";
            String yearPart = parts.length > 1 ? (deptPart + "|" + parts[1]) : "";

            if (!depts.contains(deptPart) && !years.contains(yearPart)) {
                result.add(secTarget);
            }
        }

        if (result.isEmpty()) {
            return Collections.singletonList("ALL");
        }
        Collections.sort(result);
        return result;
    }

    private static int countMatches(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) count++;
        }
        return count;
    }

    public static int estimateRecipients(List<String> targets) {
        return estimateRecipients(targets, 2880);
    }

    public static int estimateRecipients(List<String> targets, int totalEnrolledStudents) {
        List<String> compressed = compressTargets(targets);
        if (compressed.contains("ALL")) {
            return totalEnrolledStudents;
        }

        int count = 0;
        for (String t : compressed) {
            String[] parts = t.split("\\|");
            switch (parts.length) {
                case 1:
                    count += 720; // entire department
                    break;
                case 2:
                    count += 180; // entire year
                    break;
                case 3:
                    count += 60;  // single section
                    break;
                default:
                    count += 60;
                    break;
            }
        }
        return Math.min(count, totalEnrolledStudents);
    }

    public static String formatTargetLabel(String target) {
        if (target == null || target.equals("ALL")) {
            return "Entire College";
        }
        if (target.contains("|")) {
            String[] parts = target.split("\\|");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) sb.append(" • ");
                sb.append(parts[i]);
            }
            return sb.toString();
        }
        return target + " Department";
    }
}
