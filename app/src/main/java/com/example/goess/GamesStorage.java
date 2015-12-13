package com.example.goess;

import android.util.Log;
import java.util.HashMap;
import java.util.LinkedList;

public class GamesStorage {

    static final int DEFAULT_GAMES_LIST_SIZE = 6;
    static final int RECENT_GAMES_LIST_SIZE = 5;

    HashMap<String, String> recentGamesByName = new HashMap<String, String>();
    HashMap<String, String> defaultGamesByName = new HashMap<String, String>();
    LinkedList<String> recentGamesQueue = new LinkedList<String>();

    GamesStorage() {
        initDefault();
    }

    private void initDefault() {
        defaultGamesByName.put("古力 vs 李世ドル (2015/10/28)", GAME_1);
        defaultGamesByName.put("My test game", GAME_2);
        defaultGamesByName.put("古力 vs 許映皓 (2015/09/08)", GAME_3);
        defaultGamesByName.put("朴廷桓 vs 古力 (2015-12-01)", GAME_4);
        defaultGamesByName.put("古力 vs 謝科 (2015-11-08)", GAME_5);
        defaultGamesByName.put("古力 vs 丁世雄 (2015-05-14)", GAME_6);
    }

    public String getDefaultGameAt(String name) {
        return defaultGamesByName.get(name);
    }

    public String getRecentGameAt(String name) {
        return recentGamesByName.get(name);
    }

    public void addRecentGame(String name, String sgf) {

        if (!recentGamesByName.containsKey(name)) {
            if (recentGamesByName.size() < RECENT_GAMES_LIST_SIZE)
                recentGamesQueue.addFirst(name);
            else {
                String last = recentGamesQueue.removeLast();
                recentGamesByName.remove(last);
                recentGamesQueue.addFirst(name);
            }
        } else {
            recentGamesQueue.remove(name);
            recentGamesQueue.addFirst(name);
        }
        recentGamesByName.put(name, sgf);
    }

    private static final String GAME_1 = "(;SZ[19]EV[2015年中国囲碁リーグ第18節]DT[2015/10/28]KM[7.5]" +
            "PB[古力]PW[李世ドル]GN[広西対重慶]PC[中国広西]RE[B+R];B[pd];W[dp];B[pq];W[dd];B[fc];W[cf];" +
            "B[qk];W[nc];B[lc];W[qc];B[qd];W[pc];B[od];W[nb];B[me];W[mp];B[po];W[jp];B[cn];W[fq];B[b" +
            "p];W[oj];B[ok];W[nk];B[nj];W[pk];B[ol];W[pj];B[pl];W[ni];B[mj];W[qj];B[mi];W[ql];B[qm];" +
            "W[rk];B[rl];W[nh];B[mh];W[ng];B[qg];W[rm];B[qk];W[mg];B[ql];W[kg];B[ph];W[pi];B[ke];W[l" +
            "h];B[mk];W[ig];B[ip];W[io];B[hp];W[jq];B[ho];W[dn];B[in];W[jo];B[cm];W[dm];B[dl];W[el];" +
            "B[dk];W[ek];B[do];W[eo];B[co];W[en];B[fm];W[dj];B[cj];W[ci];B[bk];W[bj];B[ck];W[ej];B[h" +
            "m];W[hr];B[gr];W[gq];B[ir];W[fr];B[iq];W[jn];B[jm];W[jr];B[js];W[ks];B[is];W[lr];B[km];" +
            "W[gn];B[hn];W[il];B[ij];W[hk];B[im];W[kj];B[nq];W[mn];B[kn];W[ko];B[ln];W[lo];B[no];W[n" +
            "p];B[op];W[id];B[nr];W[lq];B[mo];W[mq];B[rc];W[rb];B[rd];W[jc];B[db];W[cc];B[cb];W[gb];" +
            "B[bc];W[bd];B[dc];W[cd];B[gc];W[hb];B[ac];W[of];B[oe];W[fb];B[lf];W[ri];B[ed];W[lb];B[s" +
            "k];W[lg];B[pf];W[eb];B[ec];W[da];B[df];W[ba];B[bb];W[ca];B[cg];W[de];B[ef];W[bg];B[bf];" +
            "W[be];B[ce];W[ll];B[ml];W[cf];B[ea];W[fa];B[ce];W[rg];B[cf];W[qh];B[rf];W[qe];B[sg];W[r" +
            "h];B[re];W[sc];B[ie];W[ld];B[jd];W[kd];B[je];W[nd];B[rj];W[si];B[ic];W[ib];B[hd];W[md];" +
            "B[ne];W[le];B[kf];W[mf];B[hh];W[jk];B[jh];W[ji];B[ii];W[ih];B[sd];W[sf];B[qb];W[pb];B[s" +
            "b];W[qa];B[jj])";

    private static final String GAME_2 = "(;GM[1]FF[4]CA[UTF-8]AP[CGoban:3]ST[2]" +
            "RU[Japanese]SZ[19]KM[0.00]PW[White]PB[Black]" +
            "(;B[ji](;W[jh];B[nf](;W[ii];B[oh];W[ki];B[qk];W[jj];B[ij];W[ne];B[jk]" +
            ";W[of];B[kj];W[og];B[ji];W[mf];B[ng];W[nh];B[ph];W[mg];B[oi];W[ni]" +
            ";B[oj];W[nj];B[pj];W[ok];B[lk];W[qg];B[nn];W[qh];B[ml];W[pg];B[mm];W[pk]" +
            ";B[jn];W[qj];B[jl];W[pi])(;W[ng];B[nh];W[og];B[mg];W[ki];B[of];W[ii]" +
            ";B[pg];W[mh];B[jj];W[ij];B[oh]))(;W[jj]))(;B[fm];W[mn];B[mk];W[ql];B[qo]))";

    private static final String GAME_3 = "(;EV[第20回三星火災杯世界囲碁マスターズ本戦1回戦第1戦]DT[2015/09/08]" +
            "KM[6.5]PB[古力]PW[許映皓]PC[中国北京珠三角JWマリオットホテル]RE[W+R];B[pd];W[dp];B[pp];W[dd];B[fq];" +
            "W[cn];B[fc];W[hc];B[nc];W[fd];B[pj];W[nq];B[lq];W[no];B[pn];W[kp];B[oq];W[nr];B[kq];W[jp];B[lp];" +
            "W[lo];B[mp];W[qq];B[pq];W[qo];B[po];W[qp];B[qn];W[rn];B[rm];W[ro];B[qr];W[rr];B[mo];W[ln];B[mn];" +
            "W[lm];B[pr];W[rl];B[qm];W[rs];B[fo];W[eq];B[fr];W[fm];B[hn];W[hm];B[im];W[in];B[ho];W[il];B[jm];" +
            "W[jl];B[gm];W[hl];B[gl];W[io];B[do];W[co];B[cq];W[dl];B[cc];W[dc];B[cd];W[ce];B[be];W[cb];B[bb];" +
            "W[bd];B[bc];W[bf];B[db];W[eb];B[ca];W[fb];B[fl];W[rd];B[qd];W[re];B[rc];W[qg];B[qi];W[og];B[kc];" +
            "W[ej];B[cf];W[de];B[ad];W[bg];B[dq];W[eo];B[en];W[dn];B[fp];W[jq];B[eh];W[gi];B[dj];W[dk];B[gh];" +
            "W[hh];B[fj];W[fi];B[ei];W[ek];B[fh];W[hi];B[ch];W[cg];B[gf];W[cj];B[hg];W[rb];B[qc];W[jd];B[kd];" +
            "W[ke];B[gd];W[fe];B[je];W[ge];B[jf];W[jc];B[le];W[kb];B[lb];W[kf];B[he];W[lf];B[hd];W[gc];B[jb];" +
            "W[ld];B[lc];W[ib];B[ih];W[kh];B[ii];W[gk];B[md];W[ja];B[ki];W[li];B[kj];W[lj];B[lk];W[mk];B[kl];" +
            "W[km];B[mj];W[nj];B[mi];W[lh];B[ni];W[jk];B[kk];W[jj];B[ji];W[oi];B[nh];W[ng];B[oj];W[jg];B[ig];" +
            "W[ml];B[ij];W[hk];B[jh];W[fg];B[gg];W[eg];B[qf];W[rf];B[rh];W[rg];B[nk];W[pg];B[bh];W[ci];B[dh];" +
            "W[mg];B[fk];W[hj];B[bk];W[bj];B[aj];W[bl];B[mf];W[if];B[bi];W[ck];B[ie];W[lg])";

    private static final String GAME_4 = "(;US[棋聖道場]WR[九段]KM[6.5]DT[2015-12-01]EV[第17回農心辛拉面杯三国囲棋擂" +
            "臺戦第9局]PC[韓国釜山]PB[朴廷桓]SO[]RE[白両目半勝]BR[九段]PW[古力]SZ[19];B[pd];W[dd];B[qp];W[dq];B[oq];" +
            "W[co];B[nc];W[fc];B[pj];W[jq];B[ci];W[ck];B[bd];W[cf];B[bf];W[cg];B[bg];W[ch];B[bh];W[di];B[cj];" +
            "W[dj];B[bk];W[bl];B[jd];W[jo];B[il];W[re];B[qd];W[qh];B[qi];W[ph];B[ni];W[pf];B[nf];W[oj];B[oi];" +
            "W[pi];B[qj];W[ok];B[ql];W[lk];B[og];W[ri];B[rj];W[rg];B[si];W[sh];B[rh];W[rd];B[rc];W[ri];B[pg];" +
            "W[qg];B[rh];W[mg];B[ng];W[ri];B[sj];W[ij];B[eq];W[ep];B[fp];W[fq];B[er];W[fr];B[gp];W[hq];B[gk];" +
            "W[ak];B[km];W[in];B[dk];W[bj];B[bi];W[dl];B[ek];W[el];B[jj];W[fk];B[ii];W[ln];B[ce];W[de];B[cc];" +
            "W[sf];B[qf];W[qe];B[pe];W[sd];B[rh];W[dc];B[cb];W[ri];B[ej];W[fj];B[ei];W[dh];B[fi];W[gj];B[eg];" +
            "W[ef];B[eh];W[df];B[hh];W[hj];B[kk];W[hc];B[hm];W[gh];B[gg];W[fg];B[fh];W[im];B[ff];W[jl];B[ic];" +
            "W[ib];B[jb];W[id];B[jc];W[fe];B[gd];W[hf];B[hd];W[hb];B[fd];W[gf];B[gi];W[ie];B[ge];W[fg];B[fl];" +
            "W[jk];B[ff];W[cd];B[be];W[fg];B[cm];W[cl];B[ff];W[bb];B[bc];W[fg];B[ik];W[kj];B[ji];W[hl];B[ff];" +
            "W[ee];B[if];W[jf];B[ig];W[je];B[he];W[fg];B[kl];W[hk];B[ff];W[kd];B[ke];W[kf];B[hg];W[lc];B[ec];" +
            "W[fb];B[eb];W[rb];B[qc];W[rh];B[mm];W[lm];B[ll];W[ml];B[kn];W[pm];B[nl];W[qm];B[pl];W[nm];B[ol];" +
            "W[mn];B[mk];W[mq];B[mp];W[np];B[nq];W[lp];B[mr];W[mo];B[om];W[pp];B[po];W[op];B[pq];W[qo];B[ro];" +
            "W[qq];B[qn];W[qr];B[lq];W[rp];B[qo];W[mp];B[pr];W[os];B[rr];W[rq];B[ps];W[rs];B[ns];W[sr];B[fa];" +
            "W[kb];B[kr];W[on];B[ir];W[hr];B[pn];W[jr];B[mb];W[sc];B[ha];W[ia];B[gb];W[kc];B[gc];W[ja];B[qb];" +
            "W[rm];B[oo];W[nn];B[rl];W[lh];B[li];W[kh];B[js];W[iq];B[ki];W[is];B[md];W[me];B[mf];W[le];B[ko];" +
            "W[kp];B[mh];W[ks];B[ls];W[ne];B[nd];W[so];B[sm];W[oe];B[of];W[la];B[lf];W[lg];B[ld];W[lb];B[od];" +
            "W[ke];B[jh];W[kg];B[ma];W[oh];B[nh];W[mc];B[nb];W[kq];B[lr];W[mm];B[ai];W[aj];B[pf];W[rf];B[ra];" +
            "W[sb];B[ga];W[js];B[lj];W[jn];B[sn];W[sp];B[qa];W[jm];B[hi];W[no];B[rn];W[lo];B[qs];W[jg];B[sa])";

    private static final String GAME_5 = "(;KM[7.5]PW[謝科]BR[九段]WR[二段]US[棋聖道場]DT[2015-11-08]EV[2015金立智" +
            "能手機杯囲甲第19局（快棋）]PC[中国]PB[古力]SO[]RE[白2又1/4子勝]SZ[19];B[pd];W[dd];B[pq];W[dq];B[qo];W[jp]" +
            ";B[do];W[eo];B[en];W[ep];B[dm];W[nq];B[cf];W[fc];B[gn];W[gp];B[bd];W[cc];B[dh];W[oo];B[nc];W[pm];B[hc]" +
            ";W[rm];B[np];W[op];B[nr];W[oq];B[or];W[mq];B[mr];W[qq];B[pp];W[qr];B[pr];W[ro];B[lq];W[qp];B[po];W[on]" +
            ";B[jq];W[iq];B[rn];W[qn];B[kq];W[ir];B[bc];W[he];B[jd];W[df];B[dg];W[ef];B[fh];W[qf];B[lo];W[lm];B[rd]" +
            ";W[of];B[jf];W[me];B[gd];W[hb];B[fd];W[ed];B[gc];W[gb];B[fb];W[ec];B[ib];W[eb];B[gf];W[kg];B[cb];W[ce]" +
            ";B[be];W[gg];B[hg];W[ke];B[md];W[ld];B[ne];W[mc];B[nd];W[hf];B[je];W[ge];B[mf];W[mg];B[le];W[lf];B[ng]" +
            ";W[nf];B[me];W[nh];B[pg];W[og];B[pf];W[ph];B[qg];W[qh];B[rg];W[ic];B[jc];W[id];B[ha];W[fa];B[cp];W[cq]" +
            ";B[bq];W[cn];B[dn];W[jb];B[kb];W[fj];B[dj];W[rh];B[br];W[jr];B[kr];W[ia];B[es];W[jn];B[ks];W[ka];B[mb]" +
            ";W[lb];B[kc];W[ei];B[di];W[gl];B[fr];W[er];B[dr];W[fq];B[eq];W[cg];B[bf];W[er];B[sh];W[si];B[sg];W[el]" +
            ";B[fm];W[cr];B[eq];W[dp];B[cs];W[ck];B[fo];W[er];B[fp];W[ds];B[gq];W[bs];B[co];W[gr];B[eq];W[fs];B[fl]" +
            ";W[fk];B[ek];W[hh];B[im];W[il];B[mm];W[ln];B[jm];W[jl];B[ri];W[sj];B[oi];W[oh];B[pn];W[qm];B[kl];W[ll]" +
            ";B[hl];W[hm];B[hk];W[in];B[ij];W[gi];B[ji];W[km];B[jg];W[kh];B[ig];W[eh];B[lj];W[kj];B[ki];W[li];B[kk]" +
            ";W[mj];B[jj];W[mi];B[rp];W[sn];B[rr];W[rq];B[qs];W[sp];B[no];W[mn];B[nn];W[om];B[fq];W[hq];B[lp];W[ma]" +
            ";B[lc];W[go];B[fn];W[na];B[la];W[bm];B[bj];W[lb];B[hs];W[gs];B[la];W[cj];B[bi];W[lb];B[nm];W[pb];B[la]" +
            ";W[pe];B[oe];W[lb];B[qc];W[nl];B[la];W[ci];B[ch];W[lb];B[ml];W[mk];B[la];W[ps];B[os];W[lb];B[ol];W[nk]" +
            ";B[la];W[qe];B[se];W[lb];B[rj];W[sk];B[la];W[rf];B[sf];W[lb];B[rk];W[sl];B[la];W[ms];B[ls];W[lb];B[rl]" +
            ";W[sm];B[la];W[eg];B[ej];W[lb];B[qb];W[la];B[ob];W[oa];B[pc];W[db];B[pa];W[ib];B[ba];W[kf];B[ho];W[hj]" +
            ";B[js];W[is];B[gk];W[ik];B[gm];W[gj];B[gl];W[hp];B[hn];W[io];B[cd];W[gh];B[jh];W[ar];B[bp];W[rs];B[ss]" +
            ";W[sr];B[sq];W[sd];B[sc];W[sr];B[nb];W[ja];B[sq];W[re];B[qd];W[sr];B[kd];W[ko];B[sq];W[bh];B[bg];W[sr]" +
            ";B[lh];W[lg];B[sq];W[dk];B[ah];W[sr];B[de];W[rs];B[im];W[aq];B[da];W[cs];B[ap];W[as];B[ee];W[fe];B[ce]" +
            ";W[lk];B[kj];W[dc];B[ps];W[ea];B[ca];W[jm];B[hm];W[ii];B[ih];W[ie];B[ns];W[kp];B[mp];W[mo];B[jk];W[if];" +
            "B[hi])";

    private static final String GAME_6 = "(;EV[2015金立智能手機杯囲甲第3局]PC[中国重慶]DT[2015-05-14]SZ[19]KM[7.5]US[棋聖" +
            "道場]SO[]PB[古力]BR[九段]PW[丁世雄]WR[三段]RE[黒中盤勝];B[pd];W[dd];B[pq];W[cp];B[eq];W[qn];B[qp];W[qk];B[qi]" +
            ";W[ok];B[qf];W[iq];B[do];W[co];B[dn];W[cm];B[cq];W[dm];B[dp];W[fm];B[cn];W[bn];B[bq];W[cf];B[ic];W[gc]" +
            ";B[ie];W[ge];B[ig];W[ob];B[pb];W[pc];B[oc];W[qc];B[qb];W[od];B[nc];W[qd];B[pe];W[nb];B[rb];W[mc];B[nd]" +
            ";W[me];B[ne];W[nf];B[kc];W[md];B[oe];W[jd];B[id];W[lb];B[eb];W[ec];B[kd];W[lg];B[kh];W[jf];B[if];W[lh]" +
            ";B[cc];W[fb];B[gh];W[cd];B[ki];W[ei];B[nj];W[nh];B[kq];W[io];B[on];W[oj];B[gm];W[fl];B[gn];W[ko];B[mq]" +
            ";W[im];B[jn];W[jo];B[pm];W[qm];B[pl];W[qj];B[ql];W[rl];B[rm];W[rn];B[rk];W[sm];B[rj];W[rp];B[rq];W[ro]" +
            ";B[nk];W[oi];B[km];W[in];B[gl];W[gk];B[fj];W[ej];B[og];W[ng];B[fg];W[ef];B[fk];W[ek];B[hb];W[gb];B[bo]" +
            ";W[mo];B[bm];W[bl];B[an];W[ck];B[op];W[kl];B[lm];W[hk];B[fi];W[fn];B[go];W[kk];B[nl];W[gq];B[el];W[dl]" +
            ";B[gp];W[jq];B[hp];W[kr];B[lr];W[kp];B[lq];W[fr];B[ip];W[jp];B[gd];W[fd];B[kj];W[ik];B[jm];W[jl];B[hc]" +
            ";W[ga];B[pk];W[er];B[dr];W[js];B[oa];W[kb];B[jb];W[ma];B[pj];W[of];B[pg];W[pf];B[qe];W[je];B[le];W[lf]" +
            ";B[jg];W[kf];B[jc];W[mi];B[eg];W[dg];B[eh];W[dh];B[ni];W[mj];B[oh];W[fq];B[fp];W[nq];B[np];W[mk];B[ml]" +
            ";W[nr];B[mp];W[ls];B[ms];W[mr];B[ks];W[gf];B[ii];W[ls];B[db];W[bc];B[ks];W[qg];B[ph];W[ls];B[bb];W[dc]" +
            ";B[ks];W[hi];B[hh];W[ls];B[cb];W[bd];B[ks];W[gi];B[ja];W[ls];B[ab];W[ba];B[ks];W[jj];B[ji];W[ls];B[ea]" +
            ";W[ca];B[ks];W[ha];B[ia];W[ls];B[sk];W[sl];B[ks];W[hg];B[ih];W[ls];B[lk];W[ll];B[ks];W[qh];B[pi];W[ls]" +
            ";B[lj])";
}
