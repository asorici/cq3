\documentclass[a4paper,12pt]{article}
\usepackage[landscape]{geometry}
\usepackage[utf8x]{inputenc}
\usepackage{multirow}
\usepackage{color}
\usepackage[usenames,dvipsnames,table]{xcolor}
\usepackage{float}

\begin{document}

\begin{center}
  {\huge \textbf{AI-MAS Winter Olympics: Crafting Quest 3}} \\
  {\huge Qualifications: Team \textbf{ {{escapedteamname}} } Report}
\end{center}


\section{Interpreting Results}
\label{sec:overview}

\par Section \ref{sec:overview} presents a general overview of the
results and your team's position on the board. The score in Table
\ref{table:overview} is computed by the following rules:
\begin{itemize}
\item Each win is worth 2 points. A game is won by the team obtaining
  the highest score.
\item Each draw is worth 1 point. A game is considered a draw if both
  teams obtain the same score.
\item Each lost game is worth 0 points, the loosing team being the one
  obtaining the lowest score.
\end{itemize}


The Crafting Quest 3 qualification round featured one-on-one matches played against all players on all 3 maps prepared for the competition. Each pair of opponents played 2 matches on each map, alternating their starting positions.

Section \ref{sec:general} shows
you the general placement of your team (number many wins, draws and
losses).  Section \ref{sec:all} gives you the detailed results of each
match outcome.

\section{Testing Round \#2 Overview}
\label{sec:general}

The first 8 teams in the general standing go through to the final.

\begin{table}[H]
  \centering
  \caption{Testing Round \#2 Overview}
  \begin{tabular}{|c|c|c|c|c|}
    \hline
      \textbf{Team Name} & \textbf{W} & \textbf{D} & \textbf{L} & \textbf{Score} \\
      \hline
      \hline
      {% for teamitem in teamscores %} 
        {% if teamitem.team_id == thisteamid %} 
          \textbf{ {{escapedteamname}} } & {{ teamitem.w }} & {{ teamitem.d }} & {{ teamitem.l }} & {{ teamitem.total_points }} \\
          \hline  
          {% else %}
            {{teamitem.ename}} & {{ teamitem.w }} & {{ teamitem.d }} & {{ teamitem.l }} & {{ teamitem.total_points }} \\
            \hline  
            {% endif %}
              {% endfor %}
                
              \end{tabular}
              \label{table:overview}
          \end{table}

          \section{Individual Match Results}
          \label{sec:all}

\begin{minipage}[b]{0.45\linewidth}
  Definitions:\\\vspace*{.5em}
  \begin{tabular}[b]{l l}
    \textbf{K}  & Kills \\
    \textbf{RK} & Retaliation Kills \\
    \textbf{DU} & Dead Units \\
    \textbf{Ptower} & Placed Towers \\
    \textbf{Strap} & Succesful Traps \\
    \textbf{Ptrap} & Placed Traps \\
    \textbf{KS} & Killing Spree \\
    \textbf{FB} & First Blood
  \end{tabular}
\end{minipage}
\hspace{0.5cm}
\begin{minipage}[b]{0.45\linewidth}
  Starting positions:\\\vspace*{.5em}
  \begin{tabular}[b] {l l}
    \textbf{ULC} & Upper Left Corner \\
    \textbf{LRC} & Lower Right Corner
  \end{tabular}
  \vspace*{1em} Results:\\\vspace*{.5em}
  \begin{tabular}[b]{l l}
    \textbf{L} & Lose \\
    \textbf{D} & Draw \\
    \textbf{W} & Win
  \end{tabular}
\end{minipage}

{% for m in maps %}

\subsection{ {{ mapnames[m] }} }
{% for o in teamstatsbymap[m][thisteamid].ops %}
  \vspace*{2em}
  \par {\large {\color{Gray} Team} {{escapedteamname}} {\color{Gray}
      vs. Team} {{o}}}
  \newline
  \begin{tabular}[t]{| c | c | c | c | c | c | c | c | c | c | c | c
      |}
    \hline
    Starting Position & \textbf{Result} & Player & \textbf{Score} & K & RK & DU & PTower & STrap & PTrap & KS & FB \\
    {% for g in teamstatsbymap[m][thisteamid].games %}
      {% if g.oponent_data[0].ename == o %}
        \hline
        \multirow{2}{*}{ {% if g.player_id == 1 %} ULC {% elif g.player_id == 2 %} LRC
            {% elif g.player_id == 3 %} URC {% else %} LLC {% endif %} } &
              \multirow{2}{*}{ {% if g.points == 2 %} \textbf{W} {% elif g.points == 1 %} 
                  \textbf{D} {% else %} \textbf{L} {% endif %} } & 
                    \cellcolor{yellow!25} You & \cellcolor{yellow!25} {{ g.total_score }} & \cellcolor{yellow!25} {{ g.kills }} &
                    \cellcolor{yellow!25} {{ g.retaliation_kills }} & \cellcolor{yellow!25} {{ g.dead_units }} & \cellcolor{yellow!25} {{ g.placed_towers }} &
                    \cellcolor{yellow!25} {{ g.successful_traps}} & \cellcolor{yellow!25} {{ g.placed_traps }} & \cellcolor{yellow!25} {{ g.killing_sprees }} &
                    \cellcolor{yellow!25} {{ g.first_blood }} \\
                    \cline{3-12}
                    & & \cellcolor{red!15} Foe & \cellcolor{red!15} {{ g.oponent_data[0].total_score }} & \cellcolor{red!15} {{ g.oponent_data[0].kills }} & \cellcolor{red!15}
                    {{ g.oponent_data[0].retaliation_kills }} & \cellcolor{red!15} {{ g.oponent_data[0].dead_units }}
                    & \cellcolor{red!15} {{ g.oponent_data[0].placed_towers }} & \cellcolor{red!15}
                    {{ g.oponent_data[0].successful_traps}} & \cellcolor{red!15} {{ g.oponent_data[0].placed_traps }} 
                    & \cellcolor{red!15} {{ g.oponent_data[0].killing_sprees }} & \cellcolor{red!15}
                    {{ g.oponent_data[0].first_blood }} \\
                    {% endif %}
                      {% endfor %}
                        \hline
                      \end{tabular}
                      {% endfor %}
\newpage
{% endfor %}
                    

\end{document}
